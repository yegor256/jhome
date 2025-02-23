/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Jhome}.
 *
 * @since 0.1.0
 */
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
        MatcherAssert.assertThat(
            String.format(
                "java binary file doesn't exist. If you run this test, then you should have a JDK installed. The file we received '%s'.%nAll files in bin folder: %n%s",
                file,
                Files.list(new Jhome().path("bin"))
                    .map(Path::toString)
                    .collect(Collectors.joining("\n"))
            ),
            file,
            FileMatchers.anExistingFile()
        );
    }

    /**
     * Here we restrict the test to run only on Java 9+.
     * Since before Java 9, the "javac" binary was shipped separately in JDK.
     * JRE pack did not contain it.
     */
    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void findsJavacOnAnyOs() throws IOException {
        final File file = new Jhome().javac().toFile();
        MatcherAssert.assertThat(
            String.format(
                "javac binary file doesn't exist. If you run this test, then you should have a JDK installed. The path that we received is '%s'.%nAll files in bin folder: %n%s",
                file,
                Files.list(new Jhome().path("bin"))
                    .map(Path::toString)
                    .collect(Collectors.joining("\n"))
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

    /**
     * Here we restrict the test to run only on Java 9+.
     * Since before Java 9, the "javac" binary was shipped separately in JDK.
     * After Java 9, we might be sure that javac is installed.
     */
    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void checksIfJavacExists() {
        MatcherAssert.assertThat(
            "Javac binary file doesn't exist, but it should",
            new Jhome().javacExists(),
            Matchers.is(true)
        );
    }
}
