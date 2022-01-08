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

package me.lorenzo0111.multilang.utils;

import com.google.gson.JsonObject;
import me.lorenzo0111.multilang.MultiLangPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexChecker {
    private static final Pattern PATTERN = Pattern.compile("<lang>([a-zA-Z0-9]+)<\\/lang>");
    private static final Pattern URL_PATTERN = Pattern.compile("[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

    private RegexChecker() {}

    public static @NotNull String replace(Player player, @NotNull JsonObject json) {
        return replace(player,json.get("text").getAsString());
    }

    public static @NotNull String replace(Player player, String string) {
        final Matcher matcher = PATTERN.matcher(string);

        final StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            final String group = matcher.group(1);

            String localized = MultiLangPlugin.getInstance()
                    .getApi()
                    .localize(player, group);

            if (localized == null) {
                localized = "&cTranslation not found";
            }

            matcher.appendReplacement(buffer, ChatColor.translateAlternateColorCodes('&', localized));
        }

        return matcher.appendTail(buffer).toString();
    }

    public static boolean isUrl(String string) {
        return URL_PATTERN.matcher(string).matches();
    }

    @Contract(pure = true)
    public static boolean contains(@NotNull String string, @NotNull List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(string).find()) {
                return true;
            }
        }
        return false;
    }
}
