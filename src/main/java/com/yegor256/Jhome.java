/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2023-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.yegor256;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

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
        this(Jhome.base());
    }

    /**
     * Constructor.
     * @param home The home.
     */
    public Jhome(final Path home) {
        this.home = home;
    }

    /**
     * Find the file inside {@code JAVA_HOME}.
     * @param loc Location, e.g. {@code "bin/java"} relative to {@code JAVA_HOME}.
     * @return The path of it
     */
    public Path path(final String... loc) {
        return Arrays.stream(loc)
            .map(subpath -> subpath.replace("/", File.separator).replace("\\", File.separator))
            .map(Paths::get)
            .reduce(this.home, Path::resolve);
    }

    /**
     * Find the {@code java} binary.
     * @return The path of it
     */
    public Path java() {
        return this.path(String.format("bin/java%s", Jhome.extension()));
    }

    /**
     * Find the {@code javac} binary.
     * @return The path of it
     */
    public Path javac() {
        if (!this.javacExists()) {
            throw new IllegalStateException(
                String.format(
                    "javac binary file doesn't exist in the home folder '%s'. Try to change home folder, or check if JDK is installed correctly.",
                    this.home
                )
            );
        }
        return this.javacPath();
    }

    /**
     * Check if {@code javac} binary exists.
     * @return True if it exists.
     */
    public boolean javacExists() {
        return Files.exists(this.javacPath());
    }

    /**
     * Find the {@code javac} binary.
     * @return The path of it.
     */
    private Path javacPath() {
        return this.path(String.format("bin/javac%s", Jhome.extension()));
    }

    /**
     * Find the extension of the executable file.
     * - On Windows it is ".exe".
     * - On Unix it is empty string.
     * @return The extension.
     */
    private static String extension() {
        final String result;
        if (System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("windows")) {
            result = ".exe";
        } else {
            result = "";
        }
        return result;
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
