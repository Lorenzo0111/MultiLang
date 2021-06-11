package me.lorenzo0111.multilang.storage;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.LocalizedString;

import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    private final MultiLangPlugin plugin;

    public StorageManager(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<LocalizedString> internal = new ArrayList<>();
    private final List<LocalizedString> external = new ArrayList<>();

    public List<LocalizedString> getInternal() {
        return internal;
    }

    public List<LocalizedString> getExternal() {
        return external;
    }

    public List<LocalizedString> getAll() {
        final List<LocalizedString> merged = new ArrayList<>();
        merged.addAll(internal);
        merged.addAll(external);

        return merged;
    }

    public void addExternal(LocalizedString str) {
        this.getExternal().add(str);
        plugin.getConfigManager().save(str.getKey(),str.getDefaultString(), str.getLocales());
    }
}
