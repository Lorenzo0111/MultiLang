package me.lorenzo0111.multilang.commands.subcommands;

import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand extends SubCommand {

    public GetCommand(Command command) {
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
            sender.sendMessage(this.format("&7Your locale is: &9" + LocalizedPlayer.from(player).getLocale().getName()));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(this.format("&cThis player does not exists."));
            return;
        }

        sender.sendMessage(this.format("&9%s&7's locale is: &9%s", target.getName(), LocalizedPlayer.from(target).getLocale().getName()));
    }
}
