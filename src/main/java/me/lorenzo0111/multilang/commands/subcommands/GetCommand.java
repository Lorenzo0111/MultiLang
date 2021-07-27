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

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.handlers.MessagesManager;
import me.lorenzo0111.pluginslib.command.ICommand;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand extends SubCommand {

    public GetCommand(ICommand<MultiLangPlugin> command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Get your language";
    }

    @Override
    public String getName() {
        return "get";
    }

    @Permission(value = "multilang.command.get",msg = "&8[&9MultiLang&8] &cYou do not have the permission to execute this command.")
    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.format("&cThis command can be performed from players only."));
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            sender.sendMessage(this.format(MessagesManager.get("current") + LocalizedPlayer.from(player).getLocale().getName()));
            return;
        }

        if (player.hasPermission("multilang.command.get.other")) {
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(this.format(MessagesManager.get("not-found")));
                return;
            }

            sender.sendMessage(this.format(MessagesManager.get("current-other"), target.getName(), LocalizedPlayer.from(target).getLocale().getName()));
        }
    }
}
