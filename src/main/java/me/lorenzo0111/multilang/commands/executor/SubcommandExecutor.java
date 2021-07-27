package me.lorenzo0111.multilang.commands.executor;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.pluginslib.ChatColor;
import me.lorenzo0111.pluginslib.audience.BukkitUser;
import me.lorenzo0111.pluginslib.command.ICommand;
import me.lorenzo0111.pluginslib.command.annotations.AnyArgument;
import me.lorenzo0111.pluginslib.command.annotations.NoArguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SubcommandExecutor {

    public static boolean execute(@NotNull ICommand<MultiLangPlugin> command, CommandSender sender, String[] args) {
        BukkitUser user = new BukkitUser(sender);

        if (command.getCustomization().getHeader() != null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', command.getCustomization().getHeader()));
        }

        if (command.getPermission() != null && command.getMessage() != null && !sender.hasPermission(command.getPermission())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', command.getMessage()));
            return true;
        }

        if (args.length > 0){
            for (me.lorenzo0111.pluginslib.command.SubCommand subcommand : command.getSubcommands()) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    subcommand.perform(user, args);
                    return true;
                }
            }

            Optional<me.lorenzo0111.pluginslib.command.SubCommand> anyArgs = command.findSubcommand(AnyArgument.class);

            if (anyArgs.isPresent()) {
                anyArgs.get().perform(user,args);
                return true;
            }

        } else {
            Optional<me.lorenzo0111.pluginslib.command.SubCommand> noArgsCommand = command.findSubcommand(NoArguments.class);

            if (noArgsCommand.isPresent()) {
                noArgsCommand.get().perform(user,args);
                return true;
            }

            String noArgs = command.getCustomization().getNoArgs(command.getCommand());

            if (noArgs != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noArgs));
            }
            return true;
        }

        String notFound = command.getCustomization().getNotFound(command.getCommand());

        if (notFound != null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notFound));
        }

        return true;
    }
}
