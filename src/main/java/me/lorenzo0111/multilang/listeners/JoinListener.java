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

package me.lorenzo0111.multilang.listeners;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.cache.PlayersCache;
import me.lorenzo0111.multilang.utils.Reflection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final MultiLangPlugin plugin;

    public JoinListener(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        PlayersCache.addCachedPlayer(plugin.getPlayerCache(), event.getPlayer(), plugin.getDatabaseManager().getUsersTable())
        .thenAccept((player) -> {
            plugin.debug(String.format("Trying to autodetect %s(%s) language..",event.getPlayer().getName(),event.getPlayer().getUniqueId()));
            String locale = Reflection.getLocale(event.getPlayer());

            if (player != null &&
                    !player.getLocale().getLocale().equals(locale))
                player.setLocale(plugin.getConfigManager().byKey(locale));
        });

        if (event.getPlayer().hasPermission("multilang.admin"))
            plugin.getUpdater().sendUpdateCheck(event.getPlayer());

    }
}
