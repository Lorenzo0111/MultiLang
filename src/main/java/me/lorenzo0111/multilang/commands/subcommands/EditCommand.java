package me.lorenzo0111.multilang.commands.subcommands;

import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
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
            commandSender.sendMessage(this.format("&cThis command can be executed from players only."));
            return;
        }

        String lang;

        // Check for @AnyArgument
        if (args[0].equalsIgnoreCase(this.getName())) {
            if (args.length != 2) {
                commandSender.sendMessage(this.format("&cTry to use /multilang edit LangName"));
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
            player.getPlayer().sendMessage(subcommand.format("&7You already have this &9language&7 as your &9primary language&7."));
            return;
        }

        if (!subcommand.getPlugin().getConfig().getStringList("languages").contains(localeName)) {
            player.getPlayer().sendMessage(subcommand.format("&7This language does not exists."));
            return;
        }

        player.setLocale(new Locale(localeName));
        player.getPlayer().sendMessage(subcommand.format("&7Language changed to &9" + localeName + "&7."));
    }

}
