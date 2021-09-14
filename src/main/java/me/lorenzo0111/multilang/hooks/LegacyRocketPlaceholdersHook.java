package me.lorenzo0111.multilang.hooks;

import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import me.lorenzo0111.rocketplaceholders.creator.Placeholder;

import java.util.List;

public class LegacyRocketPlaceholdersHook implements Hook {
    private final List<Placeholder> placeholders;

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
    public void addPlaceholder(Placeholder placeholder) {
        placeholders.add(placeholder);
    }
}
