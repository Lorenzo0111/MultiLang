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

package me.lorenzo0111.multilang.handlers;

import me.lorenzo0111.multilang.MultiLangPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Objects;

public class MessagesManager {
    private static MultiLangPlugin plugin;

    public static void setPlugin(MultiLangPlugin plugin) {
        MessagesManager.plugin = plugin;
        plugin.getLogger().info(String.format("Loaded messages.yml. (File version: %s)", plugin.getLoader().getMessagesConfig().getString("version", "1.0")));
    }

    public static void update(String path, Object value) {
        FileConfiguration config = plugin.getLoader().getMessagesConfig();
        config.set(path,value);
        try {
            config.save(plugin.getLoader().getMessagesFile());
            plugin.getLoader().reloadMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getLoader().getMessagesConfig().getString(path)));
    }

    public static String getPrefixed(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig("prefix") + Objects.requireNonNull(plugin.getLoader().getMessagesConfig().getString(path)));
    }
}
