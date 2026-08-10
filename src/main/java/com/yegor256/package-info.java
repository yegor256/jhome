/*
 * SPDX-FileCopyrightText: Copyright (c) 2023-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * There is only one class {@link com.yegor256.Jhome} that helps
 * you get access to JAVA_HOME, even if the environment variable
 * is not set (well, at least we try).
 *
 * <p>It is as simple as the following:</p>
 *
 * <pre><code> Path p = new Jhome().path("bin/javac");</code></pre>
 *
 * @since 0.0.1
 */
package com.yegor256;
