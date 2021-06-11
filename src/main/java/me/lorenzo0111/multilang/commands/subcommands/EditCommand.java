package me.lorenzo0111.multilang.commands.subcommands;

import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.handlers.MessagesManager;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.command.annotations.AnyArgument;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
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

        if (!subcommand.getPlugin().getConfig().getStringList("languages").contains(localeName)) {
            player.getPlayer().sendMessage(subcommand.format(MessagesManager.get("lang-not-found")));
            return;
        }

        player.setLocale(new Locale(localeName));
        player.getPlayer().sendMessage(subcommand.format(MessagesManager.get("changed"),localeName));
    }

}
