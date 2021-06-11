package me.lorenzo0111.multilang.commands.subcommands.admin;

import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.debug.DebugUtils;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.debugger.Debugger;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;

public class DebugCommand extends SubCommand {

    public DebugCommand(Command command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Debug options";
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(this.format("&cTry to use /adminlang debug help for a command list."));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "help":
                sender.sendMessage(this.format("&7Subcommands:"));
                sender.sendMessage(this.format("&9cache &7» Print the cache"));
                sender.sendMessage(this.format("&9save &7» Save the cache in a file"));
                break;
            case "cache":
                sender.sendMessage(this.format("&7Cache:"));
                sender.sendMessage(this.format(this.getPlugin().getPlayerCache().toString()));
                break;
            case "save":
                try {
                    File file = this.saveCache();
                    sender.sendMessage(this.format("&7Cache file saved to &9%s&7.", "cache/" + file.getName()));
                } catch (IOException e) {
                    sender.sendMessage(this.format("&cUnable to save cache file. Read the console for more information."));
                    e.printStackTrace();
                }

                break;
            case "debug":
                Debugger debugger = new Debugger(new DebugUtils(this,getPlugin()));
                debugger.debug();
                sender.sendMessage(this.format("&7Debug information printed in the &9console&7."));
                break;
            default:
                sender.sendMessage(this.format("&cUnknown subcommand, try to use /adminlang debug help for a command list."));
                break;
        }

    }

    public File saveCache() throws IOException{
        return this.getPlugin().getPlayerCache().save(new File(this.getPlugin().getCacheFolder(), "dump-" + System.currentTimeMillis() + ".json"));
    }
}
