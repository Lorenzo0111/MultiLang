package me.lorenzo0111.multilang.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lorenzo0111.rocketplaceholders.RocketPlaceholders;
import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import me.lorenzo0111.rocketplaceholders.creator.Placeholder;
import me.lorenzo0111.rocketplaceholders.creator.conditions.ConditionNode;
import me.lorenzo0111.rocketplaceholders.exceptions.InvalidConditionException;
import me.lorenzo0111.rocketplaceholders.providers.Provider;
import me.lorenzo0111.rocketplaceholders.storage.StorageManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RocketPlaceholdersHook extends Provider implements Hook {

    public RocketPlaceholdersHook(StorageManager manager) {
        super(RocketPlaceholders.getInstance(), manager);
    }

    @Override
    public void register(IRocketPlaceholdersAPI api) {
        api.registerProvider(this,"multilang");
    }

    @Override
    public void unregister(IRocketPlaceholdersAPI api) {
        this.manager.getInternalPlaceholders().clear();
        this.manager.getExternalPlaceholders().clear();
    }

    @Override
    public void addPlaceholder(@NotNull String identifier, JavaPlugin owner, @NotNull String text, @Nullable List<ConditionNode> nodes) {
        Placeholder placeholder = new Placeholder(identifier,owner,text,nodes, null);

        manager.getInternalPlaceholders().add(placeholder);
    }

    @Override
    public String parse(Placeholder placeholder, OfflinePlayer player, String text) throws InvalidConditionException {
        return placeholder.parseJS(PlaceholderAPI.setPlaceholders(player,text));
    }
}
