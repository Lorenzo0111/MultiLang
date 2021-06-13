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

package me.lorenzo0111.multilang.commands.subcommands;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.handlers.MessagesManager;
import me.lorenzo0111.multilang.utils.GuiUtils;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.command.annotations.NoArguments;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class GuiCommand extends SubCommand {

    public GuiCommand(Command command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Open the gui";
    }

    @Override
    @NoArguments
    public String getName() {
        return "gui";
    }

    @Permission("multilang.command.gui")
    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.format(MessagesManager.get("console")));
            return;
        }

        final LocalizedPlayer player = LocalizedPlayer.from((Player) sender);

        PaginatedGui gui = GuiUtils.createGui(this.format(""), MessagesManager.get("gui.title"));
        GuiUtils.setPageItems(gui);

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(22,
                ItemBuilder.from(Objects.requireNonNull(XMaterial.TORCH.parseItem()))
                        .name(Component.text(MessagesManager.get("gui.current") + player.getLocale().toString()))
                        .asGuiItem()
        );
        
        for (String locale : this.getPlugin().getConfig().getStringList("languages")) {
            String base = this.getPlugin().getLoader().getGuiConfig().getString("base." + locale);

            gui.addItem(ItemBuilder
                    .skull()
                    .name(Component.text(locale))
                    .lore(Component.text("§7Click to set"))
                    .texture(base != null ? base : this.getPlugin().getConfig("default-base"))
                    .asGuiItem(event -> {

                        EditCommand.setLang(player,locale,this);

                        gui.close(player.getPlayer());

                    }));
        }

        gui.open((Player) sender);
    }

}
