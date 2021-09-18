package me.lorenzo0111.multilang.hooks;

import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import me.lorenzo0111.rocketplaceholders.creator.conditions.ConditionNode;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Hook {
    void register(IRocketPlaceholdersAPI api);
    void unregister(IRocketPlaceholdersAPI api);

    void addPlaceholder(@NotNull String identifier, JavaPlugin owner, @NotNull String text, @Nullable List<ConditionNode> nodes);
}
