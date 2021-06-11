package me.lorenzo0111.multilang.commands;

import me.lorenzo0111.multilang.commands.subcommands.admin.AdminHelpCommand;
import me.lorenzo0111.multilang.commands.subcommands.admin.DebugCommand;
import me.lorenzo0111.multilang.commands.subcommands.admin.ReloadCommand;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.command.Customization;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AdminLangCommand extends Command implements TabExecutor {

    @SuppressWarnings("unchecked")
    @Permission(value = "multilang.admin", msg = "&8[&9MultiLang&8] &cYou do not have the permission to execute this command.")
    public AdminLangCommand(JavaPlugin plugin, String command, @Nullable Customization customization) {
        super(plugin, command, customization);

        this.addSubcommand(new ReloadCommand(this));
        this.addSubcommand(new DebugCommand(this));
        try {
            Field subcommands = this.getClass().getSuperclass().getDeclaredField("subcommands");
            subcommands.setAccessible(true);
            List<SubCommand> o = (List<SubCommand>) subcommands.get(this);
            this.addSubcommand(new AdminHelpCommand(this, o));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
