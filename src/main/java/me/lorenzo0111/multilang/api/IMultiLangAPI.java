package me.lorenzo0111.multilang.api;

import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.api.objects.LocalizedString;
import me.lorenzo0111.multilang.exceptions.ApiException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * MultiLang API Interface
 */
public interface IMultiLangAPI {

    /**
     * @param string String to add
     * @throws ApiException caused when something went wrong
     */
    void addString(LocalizedString string) throws ApiException;

    /**
     * @param player Player
     * @param locale Locale to set
     * @return The localized player instance
     */
    LocalizedPlayer setLang(Player player, Locale locale);

    /**
     * @param player Player to localize
     * @param key Key of the string
     * @return A localized string
     */
    @Nullable String localize(Player player, String key);

    /**
     * @param locale Locale of the string
     * @param key Key of the string
     * @return A localized string
     */
    @Nullable String localize(Locale locale, String key);
}
