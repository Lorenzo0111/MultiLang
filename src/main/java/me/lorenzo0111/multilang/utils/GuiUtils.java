package me.lorenzo0111.multilang.utils;

import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.components.xseries.XMaterial;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GuiUtils {

    public static PaginatedGui createGui(String title) {
        final String prefix = "&8&l» &7";
        return createGui(prefix,title);
    }

    public static PaginatedGui createGui(String prefix, String title) {
        final PaginatedGui gui = new PaginatedGui(3, ChatColor.translateAlternateColorCodes('&',prefix + title));
        gui.setDefaultClickAction(a -> a.setCancelled(true));
        return gui;
    }

    public static void setPageItems(PaginatedGui gui) {
        ItemStack item = Objects.requireNonNull(XMaterial.ARROW.parseItem(true), "Arrow does not exists.");

        GuiItem left = ItemBuilder.from(item)
                .setName("§8§l» §7Previous")
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    gui.previous();
                });
        GuiItem right = ItemBuilder.from(item)
                .setName("§8§l» §7Next")
                .asGuiItem(e -> {
                    e.setCancelled(true);
                    gui.next();
                });
        gui.setItem(21,left);
        gui.setItem(23,right);

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GLASS_PANE).setName("§r").asGuiItem());
    }

}
