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

package me.lorenzo0111.multilang.tasks;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateTask implements Runnable {
    private final MultiLangPlugin plugin;

    public UpdateTask(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getDatabaseManager()
                .getUsersTable()
                .all()
                .thenAccept((set) -> {
                    try {
                        Map<UUID, LocalizedPlayer> players = new HashMap<>();

                        while (set.next()) {
                            Player player = Bukkit.getPlayer(UUID.fromString(set.getString("uuid")));

                            if (player != null) {
                                players.put(player.getUniqueId(), new LocalizedPlayer(player, new Locale(set.getString("locale"),plugin.getConfigManager().getLocales().get(set.getString("locale")))));
                            }
                        }

                        plugin.getPlayerCache().reset();
                        players.forEach(plugin.getPlayerCache()::add);
                        set.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
    }
}
