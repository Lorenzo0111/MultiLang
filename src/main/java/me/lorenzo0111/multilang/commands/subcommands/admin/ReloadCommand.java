package me.lorenzo0111.multilang.commands.subcommands.admin;

import com.cryptomorin.xseries.XSound;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.exceptions.ReloadException;
import me.lorenzo0111.pluginslib.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(Command command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Reload the plugin";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public void handleSubcommand(CommandSender commandSender, String[] strings) {
        try {
            long time = System.currentTimeMillis();
            this.getCommand().getPlugin().reloadConfig();
            final MultiLangPlugin plugin = this.getPlugin();
            plugin.resetConnection();
            plugin.getConfigManager().unregisterAll();
            plugin.getConfigManager().register();
            plugin.getLoader().reloadGui();

            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                XSound.play(player,"ENTITY_EXPERIENCE_ORB_PICKUP");
            }


            commandSender.sendMessage(this.format(String.format("&7Plugin reloaded in &9%sms", System.currentTimeMillis() - time)));
        } catch (ReloadException ex) {
            if (commandSender instanceof Player) {
                XSound.play((Player) commandSender,"BLOCK_ANVIL_PLACE");
            }

            commandSender.sendMessage(this.format("&7An error has occurred while reloading the plugin. See the console for more information."));

            ex.printStackTrace();
        }
    }

}
