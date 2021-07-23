/*
 * This file is part of MultiLang, licensed under the MIT License.
 *
 * Copyright (c) Lorenzo0111
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.lorenzo0111.multilang.dependency;

import io.github.slimjar.app.builder.ApplicationBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class DependencyManager {
    private final ApplicationBuilder builder;

    public DependencyManager(JavaPlugin plugin) throws ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException, IOException {
        this(plugin,new File(plugin.getDataFolder(), "libs"));
    }

    public DependencyManager(JavaPlugin plugin, File directory) throws ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException, IOException {
        directory.mkdirs();

        this.builder = ApplicationBuilder
                .appending(plugin.getName())
                .downloadDirectoryPath(directory.toPath());
    }

    public long build() throws ReflectiveOperationException, IOException, URISyntaxException, NoSuchAlgorithmException {
        long before = System.currentTimeMillis();
        builder.build();
        long after = System.currentTimeMillis();
        return after - before;
    }

}
