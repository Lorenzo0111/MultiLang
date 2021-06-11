package me.lorenzo0111.multilang.commands.subcommands;

import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.commands.SubCommand;
import me.lorenzo0111.multilang.utils.GuiUtils;
import me.lorenzo0111.pluginslib.command.Command;
import me.lorenzo0111.pluginslib.command.annotations.NoArguments;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import me.mattstudios.mfgui.gui.components.util.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand extends SubCommand {

    public GuiCommand(Command command) {
        super(command);
    }

    @Override
    public String getDescription() {
        return "Open the gui";
    }

    @Override
    @NoArguments
    public String getName() {
        return "gui";
    }

    @Permission("multilang.command.gui")
    @Override
    public void handleSubcommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.format("&cThis command can be performed from players only."));
            return;
        }

        final LocalizedPlayer player = LocalizedPlayer.from((Player) sender);

        PaginatedGui gui = GuiUtils.createGui(this.format(""), "&7Languages");
        GuiUtils.setPageItems(gui);

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(22,
                ItemBuilder.from(Material.TORCH)
                        .setName("§7Current language: §9" + player.getLocale().toString())
                        .asGuiItem()
        );
        
        for (String locale : this.getPlugin().getConfig().getStringList("languages")) {
            String base = this.getPlugin().getLoader().getGuiConfig().getString("base." + locale);

            gui.addItem(ItemBuilder
                    .from(Material.PLAYER_HEAD)
                    .setName(locale)
                    .setLore("§7Click to set")
                    .setSkullTexture(base != null ? base : this.getPlugin().getConfig("default-base"))
                    .asGuiItem(event -> {

                        EditCommand.setLang(player,locale,this);

                        gui.close(player.getPlayer());

                    }));
        }

        gui.open((Player) sender);
    }

}
