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

package me.lorenzo0111.multilang.storage;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.LocalizedString;

import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private final MultiLangPlugin plugin;

    public StorageManager(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<LocalizedString> internal = new ArrayList<>();
    private final List<LocalizedString> external = new ArrayList<>();

    public List<LocalizedString> getInternal() {
        return internal;
    }

    public List<LocalizedString> getExternal() {
        return external;
    }

    public List<LocalizedString> getAll() {
        final List<LocalizedString> merged = new ArrayList<>();
        merged.addAll(internal);
        merged.addAll(external);

        return merged;
    }

    public void addExternal(LocalizedString str) {
        this.getExternal().add(str);
        plugin.getConfigManager().save(str.getKey(),str.getDefaultString(), str.getLocales());
    }
}
