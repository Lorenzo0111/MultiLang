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

package me.lorenzo0111.multilang.data;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.exceptions.DriverException;
import me.lorenzo0111.pluginslib.dependency.objects.Dependency;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

public enum StorageType {
    MYSQL("com.mysql.cj.jdbc.Driver"),
    FILE("me.lorenzo0111.multilang.data.StorageType");

    private final String driver;

    StorageType(String driver) {
        this.driver = driver;
    }

    @Nullable
    public CompletableFuture<Void> install(MultiLangPlugin plugin) {
        try {

            switch (this) {
                case MYSQL:
                    plugin.getDependencyManager()
                            .addDependency(new Dependency("mysql", "mysql-connector-java", "8.0.24", MultiLangPlugin.MAVEN));
                    break;
                case FILE:
                    plugin.getDependencyManager()
                            .addDependency(new Dependency("org.xerial", "sqlite-jdbc", "3.34.0", MultiLangPlugin.MAVEN));
                    break;
            }

            return plugin.getDependencyManager()
                    .installAll();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String getClassName() {
        return driver;
    }

    public Class<?> getDriver() throws DriverException {
        try {
            return Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            throw new DriverException(this);
        }
    }
}
