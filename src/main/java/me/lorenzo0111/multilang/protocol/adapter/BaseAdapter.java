package me.lorenzo0111.multilang.protocol.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonElement;
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

    public void handle(Player player, @NotNull WrappedChatComponent component) {
        JsonObject json = new JsonParser().parse(component.getJson()).getAsJsonObject();

        if (json.has("text")) {
            this.update(json, RegexChecker.replace(player, json));
        }

        // Iterate "extra", check if it has some text and update it
        if (json.has("extra")) {

            for (JsonElement element : json.get("extra").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                if (!object.has("text")) continue;

                this.update(object, RegexChecker.replace(player,object));
            }

        }

        component.setJson(json.toString());
    }

    protected void update(@NotNull JsonObject object, String value) {
        object.remove("text");
        object.addProperty("text",value);
    }

}
