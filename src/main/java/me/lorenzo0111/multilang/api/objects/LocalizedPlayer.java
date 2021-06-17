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

package me.lorenzo0111.multilang.api.objects;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.pluginslib.database.DatabaseSerializable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocalizedPlayer implements DatabaseSerializable {
    private final Player player;
    private Locale locale;

    public LocalizedPlayer(Player player, Locale locale) {
        this.player = player;
        this.locale = locale;
    }

    public static LocalizedPlayer from(Player player) {
        LocalizedPlayer localizedPlayer = MultiLangPlugin.getInstance().getPlayerCache().get(player.getUniqueId());
        return localizedPlayer == null ? new LocalizedPlayer(player,new Locale(MultiLangPlugin.getInstance().getConfig("default").toLowerCase())) : localizedPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        MultiLangPlugin.getInstance().getDatabaseManager().updateUser(this);
        MultiLangPlugin.getInstance().getPlayerCache().remove(this.getPlayer().getUniqueId());
        MultiLangPlugin.getInstance().getPlayerCache().add(this.getPlayer().getUniqueId(), this);
    }

    @Override
    public DatabaseSerializable from(Map<String, Object> keys) {
        Player player = Bukkit.getPlayer(UUID.fromString((String) keys.get("uuid")));
        Locale locale = new Locale((String) keys.get("locale"));

        return new LocalizedPlayer(player,locale);
    }

    @Override
    public @NotNull String tableName() {
        return "multilang_players";
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String,Object> map = new HashMap<>();
        map.put("uuid",player.getUniqueId());
        map.put("locale",locale.getName());
        return map;
    }

    @Override
    public String toString() {
        return "{" +
                "player=" + player +
                ", locale=" + locale +
                '}';
    }
}
