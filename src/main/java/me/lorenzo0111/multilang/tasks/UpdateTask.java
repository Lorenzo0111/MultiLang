package me.lorenzo0111.multilang.tasks;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.data.StorageType;
import me.lorenzo0111.multilang.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UpdateTask implements Runnable {
    private final MultiLangPlugin plugin;

    public UpdateTask(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final DatabaseManager database = plugin.getDatabaseManager();

                final PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM players;");
                final ResultSet set = statement.executeQuery();

                Map<UUID, LocalizedPlayer> players = new HashMap<>();

                while (set.next()) {
                    Player player = Bukkit.getPlayer(UUID.fromString(set.getString("uuid")));

                    if (player != null) {
                        players.put(player.getUniqueId(), new LocalizedPlayer(player,new Locale(set.getString("locale"))));
                    }
                }

                plugin.getPlayerCache().reset();
                players.forEach(plugin.getPlayerCache()::add);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
