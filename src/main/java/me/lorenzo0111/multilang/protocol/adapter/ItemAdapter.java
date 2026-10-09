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
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemAdapter extends BaseAdapter {
    private static final String TAG = "<lang>";
    private final MultiLangPlugin plugin;
    private boolean failed;

    public ItemAdapter(MultiLangPlugin plugin, ListenerPriority listenerPriority) {
        super(plugin, listenerPriority, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(@NotNull PacketEvent event) {
        Player player = event.getPlayer();

        // Creative clients send their items back to the server, that would save the translated text in the real item
        if (event.isPlayerTemporary() || player.getGameMode() == GameMode.CREATIVE) return;

        PacketContainer packet = event.getPacket();

        try {
            StructureModifier<ItemStack> items = packet.getItemModifier();
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = this.translate(player, items.readSafely(i));
                if (item != null) items.write(i, item);
            }

            StructureModifier<List<ItemStack>> lists = packet.getItemListModifier();
            for (int i = 0; i < lists.size(); i++) {
                List<ItemStack> list = lists.readSafely(i);
                if (list == null) continue;

                List<ItemStack> translated = new ArrayList<>(list);
                boolean changed = false;

                for (int slot = 0; slot < translated.size(); slot++) {
                    ItemStack item = this.translate(player, translated.get(slot));
                    if (item == null) continue;

                    translated.set(slot, item);
                    changed = true;
                }

                if (changed) lists.write(i, translated);
            }

            // Before 1.11 the window items were stored in an array
            StructureModifier<ItemStack[]> arrays = packet.getItemArrayModifier();
            for (int i = 0; i < arrays.size(); i++) {
                ItemStack[] array = arrays.readSafely(i);
                if (array == null) continue;

                ItemStack[] translated = array.clone();
                boolean changed = false;

                for (int slot = 0; slot < translated.length; slot++) {
                    ItemStack item = this.translate(player, translated[slot]);
                    if (item == null) continue;

                    translated[slot] = item;
                    changed = true;
                }

                if (changed) arrays.write(i, translated);
            }
        } catch (Exception e) {
            // Log the full error only once to avoid spamming the console
            if (!failed) {
                failed = true;
                plugin.getLogger().log(Level.WARNING, "Unable to translate the items of an inventory", e);
                return;
            }

            plugin.debug("Unable to translate the items of an inventory: " + e);
        }
    }

    /**
     * @param player Player that will receive the item
     * @param item Item to translate
     * @return A translated copy of the item or null if there is nothing to translate
     */
    private @Nullable ItemStack translate(Player player, @Nullable ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        boolean changed = false;

        if (meta.hasDisplayName() && meta.getDisplayName().contains(TAG)) {
            meta.setDisplayName(RegexChecker.replace(player, meta.getDisplayName()));
            changed = true;
        }

        List<String> lore = meta.hasLore() ? meta.getLore() : null;
        if (lore != null) {
            List<String> translated = new ArrayList<>(lore.size());
            boolean loreChanged = false;

            for (String line : lore) {
                if (line != null && line.contains(TAG)) {
                    line = RegexChecker.replace(player, line);
                    loreChanged = true;
                }

                translated.add(line);
            }

            if (loreChanged) {
                meta.setLore(translated);
                changed = true;
            }
        }

        if (!changed) return null;

        // The original item is the one stored in the inventory, it must not be edited
        ItemStack copy = item.clone();
        copy.setItemMeta(meta);
        return copy;
    }
}
