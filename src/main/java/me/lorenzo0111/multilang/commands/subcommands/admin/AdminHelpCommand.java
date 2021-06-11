package me.lorenzo0111.multilang.commands.subcommands.admin;

import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.commands.subcommands.HelpCommand;
import me.lorenzo0111.pluginslib.command.Command;

import java.util.List;

public class AdminHelpCommand extends HelpCommand {

    public AdminHelpCommand(Command command, List<SubCommand> subCommands) {
        super(command, subCommands);
    }

    @Override
    public String formatHelp(String command, String description) {
        return this.format("&9/alang %s &8» &7%s", command, description);
    }
}
