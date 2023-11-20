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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Finds JAVA_HOME even if the environment is not set.
 *
 * <code><pre> Path p = new Jhome().path("bin/java");</pre></code>
 *
 * @since 0.0.1
 */
public final class Jhome {

    /**
     * Home.
     */
    private final Path home;

    /**
     * Ctor.
     */
    public Jhome() {
        this.home = Jhome.base();
    }

    /**
     * Find the file inside JAVA_HOME.
     * @param loc Location, e.g. "bin/java"
     * @return The path of it
     */
    public Path path(final String loc) {
        return this.home.resolve(loc);
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
