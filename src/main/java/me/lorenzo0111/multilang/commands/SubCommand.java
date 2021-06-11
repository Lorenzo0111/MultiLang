package me.lorenzo0111.multilang.commands;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.pluginslib.command.Command;
import org.bukkit.ChatColor;

public abstract class SubCommand extends me.lorenzo0111.pluginslib.command.SubCommand {

    public SubCommand(Command command) {
        super(command);
    }

    public MultiLangPlugin getPlugin() {
        return (MultiLangPlugin) this.getCommand().getPlugin();
    }

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', this.getCommand().getPlugin().getConfig().getString("prefix") + message);
    }

    public String format(String message, Object... args) {
        return this.format(String.format(message,args));
    }

    public abstract String getDescription();
}
