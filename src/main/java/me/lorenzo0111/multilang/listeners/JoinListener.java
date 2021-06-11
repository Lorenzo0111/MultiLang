package me.lorenzo0111.multilang.listeners;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.cache.PlayersCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final MultiLangPlugin plugin;

    public JoinListener(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayersCache.addCachedPlayer(plugin.getPlayerCache(), event.getPlayer(), plugin.getDatabaseManager().getUsersTable());
    }
}
