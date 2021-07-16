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

import me.lorenzo0111.multilang.api.events.ChangeLocaleEvent;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.handlers.MessagesManager;
import me.lorenzo0111.multilang.utils.Conditions;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.command.annotations.AnyArgument;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCommand extends SubCommand {

    public EditCommand(Command command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Edit your langauge";
    }

    @AnyArgument
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    @Permission(value = "multilang.command.edit", msg = "&8[&9MultiLang&8] &cYou do not have the permission to execute this command.")
    public void handleSubcommand(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.format(MessagesManager.get("console")));
            return;
        }

        String lang;

        // Check for @AnyArgument
        if (args[0].equalsIgnoreCase(this.getName())) {
            if (args.length != 2) {
                commandSender.sendMessage(this.format(MessagesManager.get("subcommands.edit")));
                return;
            }

            lang = args[1].toLowerCase();
        } else {
            lang = args[0].toLowerCase();
        }

        LocalizedPlayer player = LocalizedPlayer.from((Player) commandSender);

        setLang(player,lang,this);
    }

    public static void setLang(LocalizedPlayer player, String localeName, SubCommand subcommand) {
        if (player.getLocale().getName().equalsIgnoreCase(localeName)) {
            player.getPlayer().sendMessage(subcommand.format(MessagesManager.get("already")));
            return;
        }

        if (!subcommand.getPlugin().getConfigManager().getLocales().containsKey(localeName)) {
            player.getPlayer().sendMessage(subcommand.format(MessagesManager.get("lang-not-found")));
            return;
        }

        Locale locale = new Locale(localeName,subcommand.getPlugin().getConfigManager().getLocales().get(localeName));
        ChangeLocaleEvent event = new ChangeLocaleEvent(player,locale);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        Conditions.localeValid(event.getLocale());

        player.setLocale(event.getLocale());
        player.getPlayer().sendMessage(subcommand.format(MessagesManager.get("changed"),localeName));
    }

}
