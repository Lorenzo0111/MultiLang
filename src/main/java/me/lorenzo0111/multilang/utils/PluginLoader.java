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

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.IMultiLangAPI;
import me.lorenzo0111.multilang.api.impl.MultiLangAPI;
import me.lorenzo0111.multilang.commands.AdminLangCommand;
import me.lorenzo0111.multilang.commands.MultiLangCommand;
import me.lorenzo0111.multilang.handlers.ConfigManager;
import me.lorenzo0111.multilang.handlers.MessagesManager;
import me.lorenzo0111.pluginslib.command.Customization;
import me.lorenzo0111.pluginslib.config.ConfigExtractor;
import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class PluginLoader {
    private final MultiLangPlugin plugin;
    private FileConfiguration guiConfig;
    private FileConfiguration messagesConfig;
    private File guiFile;
    private File messagesFile;

    public PluginLoader(MultiLangPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean init() {
        plugin.getLogger().info("Hooking with RocketPlaceholders..");

        IRocketPlaceholdersAPI api = Bukkit.getServicesManager().load(IRocketPlaceholdersAPI.class);

        if (api != null) {
            plugin.getLogger().info("RocketPlaceholders hooked!");
            plugin.setRocketPlaceholdersAPI(api);
        } else {
            plugin.getLogger().severe("Unable to find RocketPlaceholdersAPI, disabling..");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }

        plugin.setConfigManager(new ConfigManager(plugin));
        plugin.getConfigManager().register();
        plugin.getConfigManager().parse();
        return true;
    }

    public void metrics() {
        new Metrics(plugin,11666)
        .addCustomChart(new SimplePie("localesCount", () -> String.valueOf(plugin.getConfigManager().getLocales().size())));
    }

    public void api() {
        plugin.getLogger().info("Initializing api..");
        final MultiLangAPI api = new MultiLangAPI(plugin);
        Bukkit.getServicesManager().register(IMultiLangAPI.class,api,plugin, ServicePriority.Normal);
        plugin.setApi(api);
    }

    public void commands() {
        plugin.getLogger().info("Registering commands..");
        String prefix = plugin.getConfig().getString("prefix");
        final Customization customization = new Customization("&8[&9MultiLang&8] &7Running &9" + plugin.getDescription().getName() + " &7v&9" + plugin.getDescription().getVersion() + " &7by &9" + plugin.getDescription().getAuthors(),prefix + "&cCommand not found. Try to use &8/$cmd help&7.",prefix + "&7Run &8/$cmd help &7for a command list.");
        AdminLangCommand acmd = new AdminLangCommand(plugin,"multilangadmin",customization);
        MultiLangCommand cmd = new MultiLangCommand(plugin,"multilang",customization);
        Objects.requireNonNull(plugin.getCommand("multilang")).setTabCompleter(cmd);
        Objects.requireNonNull(plugin.getCommand("multilangadmin")).setTabCompleter(acmd);
    }

    public void gui() {
        plugin.getLogger().info("Loading gui.yml..");
        guiFile = new File(plugin.getDataFolder(),"gui.yml");
        if (!guiFile.exists()) {
            guiFile = Objects.requireNonNull(new ConfigExtractor(MultiLangPlugin.class, plugin.getDataFolder(), "gui.yml")
                    .extract())
                    .getFile();
        }

        this.reloadGui();
    }

    public void messages() {
        plugin.getLogger().info("Loading messages.yml..");
        messagesFile = new File(plugin.getDataFolder(),"messages.yml");
        if (!messagesFile.exists()) {
            guiFile = Objects.requireNonNull(new ConfigExtractor(MultiLangPlugin.class, plugin.getDataFolder(), "messages.yml")
                    .extract())
                    .getFile();
        }

        this.reloadMessages();
        MessagesManager.setPlugin(plugin);
        MessagesManager.update("version", plugin.getDescription().getVersion());
        MessagesManager.update("gui.current-lore", "&7Click here to autodetect.");
    }

    public void reloadGui() {
        guiConfig = new YamlConfiguration();
        try {
            Objects.requireNonNull(guiFile);
            guiConfig.load(guiFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadMessages() {
        messagesConfig = new YamlConfiguration();
        try {
            Objects.requireNonNull(messagesFile);
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public MultiLangPlugin getPlugin() {
        return plugin;
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public File getMessagesFile() {
        return messagesFile;
    }
}
