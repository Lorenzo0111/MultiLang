package me.lorenzo0111.multilang.commands.subcommands.admin;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.commands.subcommands.EditCommand;
import me.lorenzo0111.multilang.utils.Reflection;
import me.lorenzo0111.pluginslib.command.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DetectCommand extends SubCommand {

    public DetectCommand(ICommand<MultiLangPlugin> command) {
        super(command);
    }

    @Override
    public String getName() {
        return "detect";
    }

    @Override
    public String getDescription() {
        return "Force language auto-detection for a user";
    }

    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getPlugin().getConfig("prefix") + "&cTry using /alang detect (Player)"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getPlugin().getConfig("prefix") + "&cThis user is not online"));
            return;
        }

        LocalizedPlayer player = LocalizedPlayer.from(target);
        EditCommand.setLang(player, Reflection.getLocale(target), this);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getPlugin().getConfig("prefix") + "&7Locale autodetected to &9" + player.getLocale().getName()));
    }
}
