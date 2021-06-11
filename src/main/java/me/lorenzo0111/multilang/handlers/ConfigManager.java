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
