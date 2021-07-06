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

import com.cryptomorin.xseries.ReflectionUtils;
import me.lorenzo0111.multilang.MultiLangPlugin;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflection {

    public static String getLocale(Player player) {

        try {

            if (ReflectionUtils.supports(12)) {
                player.getLocale();
                return logBefore(player.getLocale(), "Found player locale: %s");
            }

        } catch (Exception | Error ignored) { }

        String locale = legacyLocale(player);
        return logBefore(locale != null, locale, "Found player locale: %s");
    }

    public static String legacyLocale(Player player) {

        try {
            Class<? extends Player.Spigot> spigot = player.spigot().getClass();
            Method getLocale = spigot.getMethod("getLocale");
            Object invoke = getLocale.invoke(player.spigot());
            return (String) invoke;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }

    }

    public static <T> T logBefore(boolean condition, T object, String message) {
        if (!condition) return object;

        return logBefore(object,message);
    }

    public static <T> T logBefore(T object, String message) {
        MultiLangPlugin.getInstance().debug(String.format(message,object));
        return object;
    }

}
