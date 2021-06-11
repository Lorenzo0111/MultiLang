package me.lorenzo0111.multilang.cache;

import com.google.gson.Gson;
import me.lorenzo0111.multilang.api.objects.Cache;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.pluginslib.database.objects.Table;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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

    public static void addCachedPlayer(Cache<UUID,LocalizedPlayer> cache, Player player, Table table) {
        table.find("uuid", player.getUniqueId()).thenAccept((set) -> {
            try {
                if (set.next()) {
                    if (cache.get(player.getUniqueId()) != null) {
                        cache.remove(player.getUniqueId());
                    }
                    cache.add(player.getUniqueId(), new LocalizedPlayer(player,new Locale(set.getString("locale"))));
                    return;
                }

                cache.add(player.getUniqueId(), LocalizedPlayer.from(player));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
