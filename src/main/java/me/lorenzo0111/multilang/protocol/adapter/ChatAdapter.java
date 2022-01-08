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
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.realtime.TranslatorConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private static final List<BukkitRunnable> TASKS = new ArrayList<>();

    public ChatAdapter(MultiLangPlugin plugin, ListenerPriority listenerPriority) {
        super(plugin, listenerPriority, PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(@NotNull PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        EnumWrappers.ChatType type = packet.getChatTypes().read(0);

        WrappedChatComponent component = packet.getChatComponents().read(0);
        if (component == null) return;

        if (type.equals(EnumWrappers.ChatType.SYSTEM)) {
            this.handle(player,component);
        }

        packet.getChatComponents().write(0, component);

        TranslatorConfig translators = ((MultiLangPlugin) this.getPlugin()).getTranslators();
        if (translators.isEnabled()) {
            LocalizedPlayer p = LocalizedPlayer.from(player);

            JsonObject json = new JsonParser().parse(component.getJson()).getAsJsonObject();

            if (json.has("text")) {
                if (!translate(event, packet, translators, p, json)) return;
            }

            // Iterate "extra", check if it has some text and update it
            if (json.has("extra")) {

                for (JsonElement element : json.get("extra").getAsJsonArray()) {
                    JsonObject object = element.getAsJsonObject();
                    if (!object.has("text")) continue;

                    if (!translate(event, packet, translators, p, object)) return;
                }

            }

            component.setJson(json.toString());
        }

        packet.getChatComponents().write(0, component);
    }

    private boolean translate(PacketEvent event, PacketContainer packet, @NotNull TranslatorConfig translators, @NotNull LocalizedPlayer p, @NotNull JsonObject object) {
        String text = translators.tryFromCache(p.getLocale(), object.get("text").getAsString());
        if (text != null) {
            this.update(object,text);
        } else {
            event.setCancelled(true);
            this.queue(p, packet.deepClone());
            return false;
        }
        return true;
    }


    public void queue(LocalizedPlayer p, PacketContainer packet) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                WrappedChatComponent component = packet.getChatComponents().read(0);

                TranslatorConfig translators = ((MultiLangPlugin) ChatAdapter.this.getPlugin()).getTranslators();

                ChatAdapter.this.updateTexts(component, (value) -> {
                    String text = translators.translate(p.getLocale(), value);
                    return text != null ? text : value;
                });

                packet.getChatComponents().write(0, component);
                Bukkit.getScheduler().runTask(ChatAdapter.this.getPlugin(), () -> {
                    try {
                        ProtocolManager manager = ((MultiLangPlugin) ChatAdapter.this.getPlugin()).getProtocol().getManager();

                        manager.sendServerPacket(p.getPlayer(), packet);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });

                TASKS.remove(this);
            }
        };
        task.runTaskAsynchronously(plugin);
        TASKS.add(task);
    }

    public static List<BukkitRunnable> getTasks() {
        return TASKS;
    }
}
