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
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Finds {@code JAVA_HOME} even if the environment variable is not set.
 *
 * <p>Use it like this:</p>
 *
 * <code><pre> Path p = new Jhome().path("bin/java");</pre></code>
 *
 * <p>You will get the absolute location of the {@code bin/java}
 * executable binary, if it exists. If it doesn't exist, a runtime
 * exception will be thrown.</p>
 *
 * @since 0.0.1
 */
public final class Jhome {

    /**
     * Home, where {@code JAVA_HOME} points to.
     */
    private final Path home;

    /**
     * Ctor.
     */
    public Jhome() {
        this.home = Jhome.base();
    }

    /**
     * Get the {@code JAVA_HOME}.
     * @return The path of it
     */
    public Path path() {
        return this.home;
    }

    /**
     * Find the file inside {@code JAVA_HOME}.
     * @param loc Location, e.g. {@code "bin/java"}
     * @return The path of it
     */
    public Path path(final String loc) {
        return this.home.resolve(
            loc.replace("/", File.separator).replace("\\", File.separator)
        );
    }

    /**
     * Find home.
     * @return The path of it
     */
    private static Path base() {
        final Path base;
        final String property = System.getProperty("java.home");
        if (property == null) {
            final String environ = System.getenv("JAVA_HOME");
            if (environ == null) {
                throw new IllegalStateException(
                    "Neither java.home nor JAVA_HOME are set"
                );
            }
            base = Paths.get(environ);
        } else {
            base = Paths.get(property);
        }
        return base;
    }

}
