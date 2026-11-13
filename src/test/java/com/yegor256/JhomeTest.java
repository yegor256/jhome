/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test case for {@link Jhome}.
 *
 * @since 0.1.0
 */
@ResourceLock(Resources.SYSTEM_PROPERTIES)
final class JhomeTest {

    @Test
    void findsHome() {
        MatcherAssert.assertThat(
            "Default home should lead to an existing folder",
            new Jhome().path().toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void findsCorrectHome() {
        MatcherAssert.assertThat(
            "Bin folder should exist in the JAVA_HOME folder",
            new Jhome().path().resolve("bin").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void findsJavaOnAnyOs() throws IOException {
        final File file = new Jhome().java().toFile();
        final String listing;
        try (Stream<Path> files = Files.list(new Jhome().path("bin"))) {
            listing = files.map(Path::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        }
        MatcherAssert.assertThat(
            String.format(
                "java binary file doesn't exist. If you run this test, then you should have a JDK installed. The file we received '%s'.%nAll files in bin folder: %n%s",
                file,
                listing
            ),
            file,
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void findsJavacOnAnyOs() throws IOException {
        final File file = new Jhome().javac().toFile();
        final String listing;
        try (Stream<Path> files = Files.list(new Jhome().path("bin"))) {
            listing = files.map(Path::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        }
        MatcherAssert.assertThat(
            String.format(
                "javac binary file doesn't exist. If you run this test, then you should have a JDK installed. The path that we received is '%s'.%nAll files in bin folder: %n%s",
                file,
                listing
            ),
            file,
            FileMatchers.anExistingFile()
        );
    }

    @Test
    void throwsIfJavacNotFound(final @TempDir Path temp) {
        MatcherAssert.assertThat(
            "Exception message should contain the path to the home folder",
            Assertions.assertThrows(
                IllegalStateException.class,
                new Jhome(temp)::javac,
                "Should throw IllegalStateException if javac binary file doesn't exist"
            ).getMessage(),
            Matchers.stringContainsInOrder(
                String.format("javac binary file doesn't exist in the home folder '%s'", temp)
            )
        );
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void findsRealFileOnUnixOs() {
        MatcherAssert.assertThat(
            "java binary file doesn't exist. If you run this test, then you should have a JDK or JRE installed",
            new Jhome().path("bin/java").toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void findsRealExeFileOnWindowsOs() {
        MatcherAssert.assertThat(
            "java.exe binary file doesn't exist. If you run this test, then you should have a JDK or JRE installed",
            new Jhome().java().toFile().exists(),
            Matchers.is(true)
        );
    }

    @Test
    void getsJavaVersion() {
        MatcherAssert.assertThat(
            "Java version can't be read",
            new Jaxec()
                .with(new Jhome().java().toString())
                .with("-version")
                .exec(),
            Matchers.is(Matchers.notNullValue())
        );
    }

    @Test
    void checksIfJavacExists() {
        MatcherAssert.assertThat(
            "Javac binary file doesn't exist, but it should",
            new Jhome().javacExists(),
            Matchers.is(true)
        );
    }

    @ParameterizedTest
    @CsvSource({"Linux, javac", "Windows 11, javac.exe"})
    void findsBundledJdk(final String system, final String compiler, final @TempDir Path temp)
        throws Exception {
        final Path runtime = Files.createDirectory(temp.resolve("jre"));
        final Path binary = Files.write(
            Files.createDirectory(temp.resolve("bin")).resolve(compiler), new byte[0]
        );
        final Path tools = Files.write(
            Files.createDirectory(temp.resolve("lib")).resolve("tools.jar"), new byte[0]
        );
        final Jhome home = JhomeTest.defaultHome(runtime, system);
        MatcherAssert.assertThat(
            "Bundled JRE should resolve compiler and tools inside its JDK",
            Arrays.asList(home.path(), home.path("bin", compiler), home.path("lib/tools.jar")),
            Matchers.contains(temp, binary, tools)
        );
    }

    @ParameterizedTest
    @CsvSource({"Linux, javac.exe", "Windows 11, javac"})
    void preservesStandaloneJre(final String system, final String other, final @TempDir Path temp)
        throws Exception {
        final Path runtime = Files.createDirectory(temp.resolve("jre"));
        Files.write(Files.createDirectory(temp.resolve("bin")).resolve(other), new byte[0]);
        final Jhome home = JhomeTest.defaultHome(runtime, system);
        MatcherAssert.assertThat(
            "A JRE without a matching parent compiler should remain the home",
            Arrays.asList(home.path(), home.javacExists()),
            Matchers.contains(runtime, false)
        );
    }

    @ParameterizedTest
    @CsvSource({"Linux, javac", "Windows 11, javac.exe"})
    void preservesModernJdk(final String system, final String compiler, final @TempDir Path temp)
        throws Exception {
        final Path current = Files.createDirectory(temp.resolve("jdk"));
        Files.write(Files.createDirectory(temp.resolve("bin")).resolve(compiler), new byte[0]);
        final Path binary = Files.write(
            Files.createDirectory(current.resolve("bin")).resolve(compiler), new byte[0]
        );
        final Jhome home = JhomeTest.defaultHome(current, system);
        MatcherAssert.assertThat(
            "Modern JDK home and compiler should stay unchanged",
            Arrays.asList(home.path(), home.path("bin", compiler)),
            Matchers.contains(current, binary)
        );
    }

    private static Jhome defaultHome(final Path home, final String system) throws Exception {
        final Properties previous = System.getProperties();
        final Properties modified = new Properties();
        modified.putAll(previous);
        modified.setProperty("java.home", home.toString());
        modified.setProperty("os.name", system);
        try (
            URLClassLoader loader = new URLClassLoader(
                new URL[]{Jhome.class.getProtectionDomain().getCodeSource().getLocation()}, null
            )
        ) {
            System.setProperties(modified);
            final Class<?> type = loader.loadClass(Jhome.class.getName());
            return new Jhome(
                (Path) type.getMethod("path", String[].class)
                    .invoke(type.getConstructor().newInstance(), (Object) new String[0])
            );
        } finally {
            System.setProperties(previous);
        }
    }
}
