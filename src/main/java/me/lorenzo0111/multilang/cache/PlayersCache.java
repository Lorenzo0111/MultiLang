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

package me.lorenzo0111.multilang.cache;

import com.google.gson.Gson;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Cache;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.pluginslib.database.objects.Table;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayersCache implements Cache<UUID, LocalizedPlayer> {
    private final Map<UUID,LocalizedPlayer> cache = new HashMap<>();
    private final Gson gson = new Gson();

    @Override
    public void reset() {
        this.cache.clear();
    }

    @Override
    public void add(UUID key, LocalizedPlayer value) {
        this.cache.put(key,value);
    }

    @Override
    public boolean remove(UUID key, LocalizedPlayer value) {
        return this.cache.remove(key,value);
    }

    @Override
    public LocalizedPlayer remove(UUID key) {
        return this.cache.remove(key);
    }

    @Override
    public LocalizedPlayer get(UUID key) {
        return this.cache.get(key);
    }

    @Override
    public File save(File file) throws IOException {
        final Map<String,Locale> playerMap = new HashMap<>();
        this.cache.forEach((uuid,player) -> playerMap.put(uuid.toString(),player.getLocale()));

        FileWriter writer = new FileWriter(file);
        gson.toJson(playerMap, writer);
        writer.close();

        return file;
    }

    @Override
    public String toString() {
        return "{" +
                "cache=" + cache +
                '}';
    }

    public static CompletableFuture<LocalizedPlayer> addCachedPlayer(Cache<UUID,LocalizedPlayer> cache, Player player, Table table) {
        CompletableFuture<LocalizedPlayer> future = new CompletableFuture<>();

        table.find("uuid", player.getUniqueId()).thenAccept((set) -> {
            try {
                if (set.next()) {
                    if (cache.get(player.getUniqueId()) != null) {
                        cache.remove(player.getUniqueId());
                    }

                    LocalizedPlayer localized = new LocalizedPlayer(player,new Locale(set.getString("locale"), MultiLangPlugin.getInstance().getConfigManager().getLocales().getOrDefault(set.getString("locale"),"en_US")));
                    cache.add(player.getUniqueId(), localized);
                    future.complete(localized);
                    return;
                }

                cache.add(player.getUniqueId(), LocalizedPlayer.from(player));
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

}
