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

package me.lorenzo0111.multilang.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GuiUtils {

    public static PaginatedGui createGui(String title) {
        final String prefix = "&8&l» &7";
        return createGui(prefix,title);
    }

    public static PaginatedGui createGui(String prefix, String title) {
        final PaginatedGui gui = new PaginatedGui(3, Component.text(ChatColor.translateAlternateColorCodes('&',prefix + title)));
        gui.setDefaultClickAction(a -> a.setCancelled(true));
        return gui;
    }

    public static void setPageItems(PaginatedGui gui) {
        ItemStack item = Objects.requireNonNull(XMaterial.ARROW.parseItem(), "Arrow does not exists.");

        GuiItem left = ItemBuilder.from(item)
                .name(Component.text("§8§l» §7Previous"))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    gui.previous();
                });
        GuiItem right = ItemBuilder.from(item)
                .name(Component.text("§8§l» §7Next"))
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    gui.next();
                });
        gui.setItem(21,left);
        gui.setItem(23,right);

        gui.getFiller().fillBottom(ItemBuilder.from(Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())).name(Component.text("§r")).asGuiItem());
    }

}
