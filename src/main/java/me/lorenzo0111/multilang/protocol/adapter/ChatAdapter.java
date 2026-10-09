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
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.realtime.TranslatorConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatAdapter extends BaseAdapter implements Listener {
    private static final List<BukkitRunnable> TASKS = new ArrayList<>();
    private final Map<String, Map<Locale, String>> signed = new ConcurrentHashMap<>();
    private Field signedContent;

    public ChatAdapter(MultiLangPlugin plugin, ListenerPriority listenerPriority) {
        super(plugin, listenerPriority, supportedTypes());
    }

    private static PacketType[] supportedTypes() {
        List<PacketType> types = new ArrayList<>();
        types.add(PacketType.Play.Server.CHAT);
        if (isSignedChat()) types.add(PacketType.Play.Server.SYSTEM_CHAT);

        return types.toArray(new PacketType[0]);
    }

    private static boolean isSignedChat() {
        return PacketType.Play.Server.SYSTEM_CHAT.isSupported();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        TranslatorConfig translators = ((MultiLangPlugin) this.getPlugin()).getTranslators();
        if (!translators.isEnabled() || !event.isAsynchronous() || !isSignedChat()) return;

        String message = event.getMessage();
        Map<Locale, String> translations = new HashMap<>();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(event.getPlayer())) continue;

            Locale locale = LocalizedPlayer.from(online).getLocale();
            if (locale == null || translations.containsKey(locale)) continue;

            String text = translators.translate(locale, message);
            if (text != null) translations.put(locale, text);
        }

        if (translations.isEmpty()) return;

        signed.put(message, translations);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.getPlugin(), () -> signed.remove(message, translations), 200L);
    }

    @Override
    public void onPacketSending(@NotNull PacketEvent event) {
        if (event.isCancelled() || event.getPacket().getMeta("multilang").isPresent()) return;

        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();

        if (isSignedChat() && packet.getType().equals(PacketType.Play.Server.CHAT)) {
            this.handleSigned(player, packet);
            return;
        }

        WrappedChatComponent component = packet.getChatComponents().readSafely(0);
        if (component == null) return;

        this.handle(player, component);

        packet.getChatComponents().write(0, component);

        TranslatorConfig translators = ((MultiLangPlugin) this.getPlugin()).getTranslators();
        if (translators.isEnabled()) {
            LocalizedPlayer p = LocalizedPlayer.from(player);
            String jsonString = component.getJson();

            try {
                JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

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

                jsonString = json.toString();
            } catch (Exception ignored) {
                JsonObject object = new JsonObject();
                object.addProperty("text", jsonString);

                translate(event, packet, translators, p, object);

                jsonString = object.get("text").getAsString();
            }

            component.setJson(jsonString);
        }

        packet.getChatComponents().write(0, component);
    }

    private void handleSigned(@NotNull Player player, @NotNull PacketContainer packet) {
        if (player.getUniqueId().equals(packet.getUUIDs().readSafely(0))) return;

        String content = this.readSignedContent(packet);
        if (content == null) return;

        Map<Locale, String> translations = signed.get(content);
        if (translations == null) return;

        String text = translations.get(LocalizedPlayer.from(player).getLocale());
        if (text == null || text.equals(content)) return;

        WrappedChatComponent component = packet.getChatComponents().readSafely(0);
        if (component == null) {
            component = WrappedChatComponent.fromText(text);
        } else {
            this.updateTexts(component, (value) -> value.equals(content) ? text : value);
        }

        packet.getChatComponents().write(0, component);
    }

    private @Nullable String readSignedContent(@NotNull PacketContainer packet) {
        try {
            for (Object value : packet.getModifier().getValues()) {
                if (value == null) continue;

                if (signedContent == null) {
                    this.findSignedContent(value.getClass());
                }

                if (signedContent != null && signedContent.getDeclaringClass().isInstance(value)) {
                    return (String) signedContent.get(value);
                }
            }
        } catch (Exception e) {
            ((MultiLangPlugin) this.getPlugin()).debug("Unable to read signed chat message: " + e);
        }

        return null;
    }

    // The signed body is the only packet field made of the message and its timestamp
    private void findSignedContent(@NotNull Class<?> type) {
        Field content = null;
        boolean timestamp = false;

        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            if (field.getType().equals(String.class)) content = field;
            if (field.getType().equals(Instant.class)) timestamp = true;
        }

        if (content == null || !timestamp) return;

        content.setAccessible(true);
        signedContent = content;
    }

    private boolean translate(PacketEvent event, PacketContainer packet, @NotNull TranslatorConfig translators, @NotNull LocalizedPlayer p, @NotNull JsonObject object) {
        if (!translators.isTranslateServer() && event.isServerPacket()) return false;

        String text = translators.tryFromCache(p.getLocale(), object.get("text").getAsString());
        if (text != null) {
            this.update(object, text);
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
                try {
                    WrappedChatComponent component = packet.getChatComponents().read(0);

                    TranslatorConfig translators = ((MultiLangPlugin) ChatAdapter.this.getPlugin()).getTranslators();

                    ChatAdapter.this.updateTexts(component, (value) -> {
                        String text = translators.translate(p.getLocale(), value);
                        return text != null ? text : value;
                    });

                    packet.getChatComponents().write(0, component);
                    Bukkit.getScheduler().runTask(ChatAdapter.this.getPlugin(), () -> {
                        packet.setMeta("multilang", "true");

                        ProtocolManager manager = ((MultiLangPlugin) ChatAdapter.this.getPlugin()).getProtocol().getManager();
                        manager.sendServerPacket(p.getPlayer(), packet);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
