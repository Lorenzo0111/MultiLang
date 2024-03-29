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

package me.lorenzo0111.multilang.commands;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.pluginslib.audience.User;
import me.lorenzo0111.pluginslib.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SubCommand extends me.lorenzo0111.pluginslib.command.SubCommand {
    private final ICommand<MultiLangPlugin> command;

    public SubCommand(ICommand<MultiLangPlugin> command) {
        super(command);

        this.command = command;
    }

    public MultiLangPlugin getPlugin() {
        return this.getCommand().getPlugin();
    }

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', this.getCommand().getPlugin().getConfig().getString("prefix") + message);
    }

    public String format(String message, Object... args) {
        return this.format(String.format(message,args));
    }

    public abstract String getDescription();

    @Override
    public void handleSubcommand(User<?> user, String[] strings) {
        this.handleSubcommand((CommandSender) user.player(), strings);
    }

    public abstract void handleSubcommand(CommandSender sender, String[] args);

    @Override
    public ICommand<MultiLangPlugin> getCommand() {
        return command;
    }
}
