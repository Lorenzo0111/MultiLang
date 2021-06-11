package me.lorenzo0111.multilang.api.objects;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.pluginslib.database.DatabaseSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
    public @NotNull String tableName() {
        return "players";
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
