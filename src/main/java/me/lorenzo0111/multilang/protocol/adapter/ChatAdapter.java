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

package me.lorenzo0111.multilang.protocol.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.entity.Player;

public class ChatAdapter extends PacketAdapter {
    public ChatAdapter(MultiLangPlugin plugin, ListenerPriority listenerPriority) {
        super(plugin, listenerPriority, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        EnumWrappers.ChatType type = packet.getChatTypes().read(0);
        // Check if the packet comes from a plugin, if not stop the action.
        if (!type.equals(EnumWrappers.ChatType.SYSTEM)) return;

        // Get the component
        WrappedChatComponent component = packet.getChatComponents().read(0);
        JsonObject json = new JsonParser().parse(component.getJson()).getAsJsonObject();

        // If it has some text, update it
        if (json.has("text")) {
            this.update(json, RegexChecker.replace(player, json.get("text").getAsString()));
        }

        // Iterate "extra", check if it has some text and update it
        if (json.has("extra")) {

            for (JsonElement element : json.get("extra").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                if (!object.has("text")) continue;

                String text = object.get("text").getAsString();

                this.update(object, RegexChecker.replace(player,text));
            }

        }

        // Update the component and the packet
        component.setJson(json.toString());
        packet.getChatComponents().write(0,component);
    }

    private void update(JsonObject object, String value) {
        object.remove("text");
        object.addProperty("text",value);
    }
}
