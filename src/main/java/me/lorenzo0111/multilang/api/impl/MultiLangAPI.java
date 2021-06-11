package me.lorenzo0111.multilang.api.impl;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.IMultiLangAPI;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.api.objects.LocalizedString;
import me.lorenzo0111.multilang.exceptions.ApiException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MultiLangAPI implements IMultiLangAPI {
    private final MultiLangPlugin plugin;

    public MultiLangAPI(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void addString(LocalizedString string) throws ApiException {
        Optional<LocalizedString> first = plugin.getStorage()
                .getAll()
                .stream()
                .filter((str) -> str.equals(string))
                .findFirst();

        if (first.isPresent()) {
            throw new ApiException(String.format("LocalizedString %s already exists.", string.getKey()));
        }

        plugin.getStorage()
                .addExternal(string);
    }

    @Override
    public LocalizedPlayer setLang(Player player, Locale locale) {
        LocalizedPlayer localizedPlayer = LocalizedPlayer.from(player);
        localizedPlayer.setLocale(locale);
        return localizedPlayer;
    }

    @Override
    public @Nullable String localize(Player player, String key) {
        LocalizedPlayer locPlayer = LocalizedPlayer.from(player);
        return this.localize(locPlayer.getLocale(),key);
    }

    @Override
    public @Nullable String localize(Locale locale, String key) {
        Optional<LocalizedString> string = plugin.getStorage()
                .getAll()
                .stream()
                .filter((str) -> str.getKey().equals(key))
                .findFirst();

        return string.
                map(localizedString -> localizedString.getLocales().get(locale))
                .orElse(null);
    }

}
