package me.lorenzo0111.multilang.commands.subcommands;

import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.pluginslib.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCommand extends SubCommand {
    private final Map<String,String> commands = new HashMap<>();

    public HelpCommand(Command command, List<SubCommand> subCommands) {
        super(command);
        subCommands.forEach((it) -> commands.put(it.getName(),it.getDescription()));
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String description = commands.get(args[1]);
            if (description == null) {
                sender.sendMessage(this.format("&cThis subcommand does not exists."));
                return;
            }
            sender.sendMessage(this.formatHelp(args[1],description));
            return;
        }

        sender.sendMessage(this.format("&7Available commands:"));
        commands.forEach((name,description) -> sender.sendMessage(this.formatHelp(name,description)));

    }

    public String formatHelp(String command,String description) {
        return this.format("&9/multilang %s &8» &7%s", command, description);
    }

    @Override
    public String getDescription() {
        return "View this message";
    }
}
