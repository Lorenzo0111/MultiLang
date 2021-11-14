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

package me.lorenzo0111.multilang.commands.subcommands.admin;

import com.cryptomorin.xseries.XSound;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.commands.AdminLangCommand;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.exceptions.ReloadException;
import me.lorenzo0111.multilang.protocol.adapter.EntityAdapter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(AdminLangCommand command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Reload the plugin";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public void handleSubcommand(CommandSender commandSender, String[] strings) {
        try {
            long time = System.currentTimeMillis();
            this.getCommand().getPlugin().reloadConfig();
            final MultiLangPlugin plugin = this.getPlugin();
            plugin.resetConnection();
            plugin.getConfigManager().unregisterAll();
            plugin.getConfigManager().register();
            plugin.getLoader().reloadGui();
            plugin.getLoader().reloadMessages();
            EntityAdapter.runNow();

            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                XSound.play(player,"ENTITY_EXPERIENCE_ORB_PICKUP");
            }


            commandSender.sendMessage(this.format(String.format("&7Plugin reloaded in &9%sms", System.currentTimeMillis() - time)));
        } catch (ReloadException | SQLException | IOException ex) {
            if (commandSender instanceof Player) {
                XSound.play((Player) commandSender,"BLOCK_ANVIL_PLACE");
            }

            commandSender.sendMessage(this.format("&7An error has occurred while reloading the plugin. See the console for more information."));

            ex.printStackTrace();
        }
    }

}
