/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        final Jhome jhome = new Jhome(temp);
        final IllegalStateException exception = Assertions.assertThrows(
            IllegalStateException.class,
            jhome::javac
        );
        MatcherAssert.assertThat(
            exception.getMessage(),
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
}
