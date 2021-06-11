package me.lorenzo0111.multilang.handlers;

import me.lorenzo0111.multilang.MultiLangPlugin;
import org.bukkit.ChatColor;

import java.util.Objects;

public class MessagesManager {
    private static MultiLangPlugin plugin;

    public static void setPlugin(MultiLangPlugin plugin) {
        MessagesManager.plugin = plugin;
    }

    public static String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getLoader().getMessagesConfig().getString(path)));
    }

    public static String getPrefixed(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig("prefix") + Objects.requireNonNull(plugin.getLoader().getMessagesConfig().getString(path)));
    }
}
