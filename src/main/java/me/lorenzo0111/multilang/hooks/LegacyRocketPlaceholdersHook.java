package me.lorenzo0111.multilang.hooks;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import me.lorenzo0111.rocketplaceholders.creator.Placeholder;
import me.lorenzo0111.rocketplaceholders.creator.conditions.ConditionNode;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class LegacyRocketPlaceholdersHook implements Hook {
    private final List<Placeholder> placeholders;
    private Constructor<Placeholder> constructor;

    public LegacyRocketPlaceholdersHook(List<Placeholder> placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public void register(IRocketPlaceholdersAPI api) {
        this.unregister(api);

        for (Placeholder placeholder : placeholders) {
            api.addPlaceholder(placeholder);
        }
    }

    @Override
    public void unregister(IRocketPlaceholdersAPI api) {
        for (Placeholder placeholder : placeholders) {
            api.removePlaceholder(placeholder.getIdentifier());
        }
    }

    @Override
    public void addPlaceholder(@NotNull String identifier, JavaPlugin owner, @NotNull String text, @Nullable List<ConditionNode> nodes) {
        if (constructor == null) {
            try {
                this.constructor = Placeholder.class.getConstructor(String.class, String.class, JavaPlugin.class, String.class, List.class);
            } catch (NoSuchMethodException e) {
                MultiLangPlugin.getInstance().getLogger().severe("An error has occurred while registering a placeholder with reflection legacy support.");
            }
        }

        try {
            Placeholder placeholder = constructor.newInstance(null,identifier,owner,text,nodes);

            placeholders.add(placeholder);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            MultiLangPlugin.getInstance().getLogger().severe("An error has occurred while registering a placeholder with reflection legacy support: " + e.getMessage());
        }

    }
}
