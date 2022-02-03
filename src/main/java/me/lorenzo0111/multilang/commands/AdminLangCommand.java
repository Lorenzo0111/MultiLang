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
import me.lorenzo0111.multilang.commands.executor.SubcommandExecutor;
import me.lorenzo0111.multilang.commands.subcommands.admin.*;
import me.lorenzo0111.pluginslib.command.Customization;
import me.lorenzo0111.pluginslib.command.ICommand;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminLangCommand extends ICommand<MultiLangPlugin> implements TabExecutor {

    @SuppressWarnings("unchecked")
    @Permission(value = "multilang.admin", msg = "&8[&9MultiLang&8] &cYou do not have the permission to execute this command.")
    public AdminLangCommand(MultiLangPlugin plugin, String command, @Nullable Customization customization) {
        super(plugin, command, customization);

        this.addSubcommand(new ReloadCommand(this));
        this.addSubcommand(new DetectCommand(this));
        this.addSubcommand(new DebugCommand(this));
        this.addSubcommand(new ClearCacheCommand(this));
        try {
            Field subcommands = this.getClass().getSuperclass().getDeclaredField("subcommands");
            subcommands.setAccessible(true);
            List<SubCommand> o = (List<SubCommand>) subcommands.get(this);
            this.addSubcommand(new AdminHelpCommand(this, o));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String s) {
        Objects.requireNonNull(this.getPlugin().getCommand(s)).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return SubcommandExecutor.execute(this,sender,args);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> l = new ArrayList<>();

        if (args.length == 0 || args.length == 1)
            subcommands.forEach(s -> l.add(s.getName()));

        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            l.add("debug");
            l.add("help");
            l.add("cache");
            l.add("save");
        }

        return l;
    }
}
