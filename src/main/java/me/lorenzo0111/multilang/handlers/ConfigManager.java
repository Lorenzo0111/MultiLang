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

package me.lorenzo0111.multilang.handlers;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedString;
import me.lorenzo0111.multilang.exceptions.ConfigException;
import me.lorenzo0111.multilang.exceptions.ReloadException;
import me.lorenzo0111.multilang.requirements.LangRequirement;
import me.lorenzo0111.rocketplaceholders.creator.Placeholder;
import me.lorenzo0111.rocketplaceholders.creator.conditions.ConditionNode;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigManager {
    private final MultiLangPlugin plugin;
    private final List<Placeholder> addedPlaceholders = new ArrayList<>();
    public ConfigManager(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    public void parse() throws ConfigException {

        String defaultString = plugin.getConfig("default");

        if (defaultString == null || !plugin.getConfig().getStringList("languages").contains(defaultString)) {
            throw new ConfigException("Default language does not exists.");
        }

    }

    public void register() {
        final long before = System.currentTimeMillis();
        final ConfigurationSection langSection = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("strings"));

        for (String key : langSection.getKeys(false)) {
            final ConfigurationSection section = Objects.requireNonNull(langSection.getConfigurationSection(key));
            final String identifier = section.getString("identifier");
            final String defaultText = section.getString("default");
            final Map<Locale,String> localesMap = new HashMap<>();

            for (String localeKey : Objects.requireNonNull(section.getConfigurationSection("locales")).getKeys(false)) {
                if (plugin.getConfig().getStringList("languages").contains(localeKey.toLowerCase())) {
                    localesMap.put(new Locale(localeKey),section.getString("locales." + localeKey));
                } else {
                    throw new ReloadException("Locale " + localeKey + " does not exists. Please add it in languages section");
                }
            }

            this.save(identifier,defaultText,localesMap);
            this.plugin.getStorage().getInternal().add(new LocalizedString(identifier,defaultText,localesMap));

        }

        final long time = System.currentTimeMillis() - before;

        plugin.getLogger().info("Loaded all placeholders in " + time + "ms");
    }

    public List<String> getLocales() {
        return plugin.getConfig().getStringList("languages");
    }

    public void save(String identifier,String defaultText,Map<Locale,String> localesMap) {
        List<ConditionNode> conditions = new ArrayList<>();

        localesMap.forEach((locale,string) -> conditions.add(new ConditionNode(new LangRequirement(plugin,locale),string)));

        Placeholder placeholder = new Placeholder(null,identifier,plugin,defaultText,conditions);

        plugin.getRocketPlaceholdersAPI().addPlaceholder(placeholder);

        addedPlaceholders.add(placeholder);
    }

    public void unregisterAll() {
        addedPlaceholders.forEach(item -> plugin.getRocketPlaceholdersAPI().removePlaceholder(item.getIdentifier()));
        this.plugin.getStorage().getInternal().clear();
    }
}
