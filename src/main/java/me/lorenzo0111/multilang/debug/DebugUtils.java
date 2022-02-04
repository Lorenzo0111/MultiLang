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

package me.lorenzo0111.multilang.debug;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.commands.subcommands.admin.DebugCommand;
import me.lorenzo0111.multilang.protocol.adapter.ChatAdapter;
import me.lorenzo0111.pluginslib.debugger.Debuggable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DebugUtils implements Debuggable {
    private final MultiLangPlugin plugin;
    private final DebugCommand cmd;

    public DebugUtils(DebugCommand cmd, MultiLangPlugin plugin) {
        this.plugin = plugin;
        this.cmd = cmd;
    }

    @Override
    public @Nullable Map<String, Object> getKeys() {
        Map<String,Object> map = new HashMap<>();
        String directory = "Unable to save cache.";
        try {
            directory = cmd.saveCache().getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("cache",directory);
        map.put("realtime", ChatAdapter.getTasks().size());
        return map;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
