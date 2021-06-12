/*
 * This file is part of MultiLang, licensed under the MIT License.
 *
 * Copyright (c) Lorenzo0111
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
