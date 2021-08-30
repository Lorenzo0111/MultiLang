package me.lorenzo0111.multilang.protocol.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class BaseAdapter extends PacketAdapter {

    public BaseAdapter(MultiLangPlugin plugin, ListenerPriority listenerPriority, PacketType type) {
        super(plugin, listenerPriority, type);
    }

    public void handle(Player player, @NotNull WrappedChatComponent component, @NotNull Runnable save) {
        JsonObject json = new JsonParser().parse(component.getJson()).getAsJsonObject();

        this.update(json, RegexChecker.replace(player, json));

        component.setJson(json.toString());

        save.run();
    }

    protected void update(@NotNull JsonObject object, String value) {
        object.remove("text");
        object.addProperty("text",value);
    }

}
