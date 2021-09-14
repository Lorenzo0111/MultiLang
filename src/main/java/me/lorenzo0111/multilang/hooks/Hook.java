package me.lorenzo0111.multilang.hooks;

import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import me.lorenzo0111.rocketplaceholders.creator.Placeholder;

public interface Hook {
    void register(IRocketPlaceholdersAPI api);
    void unregister(IRocketPlaceholdersAPI api);

    void addPlaceholder(Placeholder placeholder);
}
