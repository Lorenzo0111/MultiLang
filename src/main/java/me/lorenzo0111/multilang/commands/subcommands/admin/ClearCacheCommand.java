package me.lorenzo0111.multilang.commands.subcommands.admin;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.pluginslib.command.ICommand;
import org.bukkit.command.CommandSender;

public class ClearCacheCommand extends SubCommand {

    public ClearCacheCommand(ICommand<MultiLangPlugin> command) {
        super(command);
    }

    @Override
    public String getName() {
        return "clearCache";
    }

    @Override
    public String getDescription() {
        return "Clear the realtime cache";
    }

    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {
        if (!this.getPlugin().getTranslators().isEnabled() || !this.getPlugin().getTranslators().isUseCache()) {
            sender.sendMessage(this.format("&cThe realtime cache is disabled"));
            return;
        }

        this.getPlugin().getTranslators().clearCache(this.getPlugin().getDatabaseManager());
        sender.sendMessage(this.format("&aThe realtime cache has been cleared"));
    }
}
