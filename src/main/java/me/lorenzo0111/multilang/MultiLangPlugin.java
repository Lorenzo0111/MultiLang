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

package me.lorenzo0111.multilang;

import com.comphenix.protocol.ProtocolLibrary;
import me.lorenzo0111.multilang.api.IMultiLangAPI;
import me.lorenzo0111.multilang.api.objects.Cache;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.cache.PlayersCache;
import me.lorenzo0111.multilang.data.StorageType;
import me.lorenzo0111.multilang.database.DatabaseManager;
import me.lorenzo0111.multilang.exceptions.ApiException;
import me.lorenzo0111.multilang.exceptions.ReloadException;
import me.lorenzo0111.multilang.handlers.ConfigManager;
import me.lorenzo0111.multilang.hooks.Hook;
import me.lorenzo0111.multilang.listeners.JoinListener;
import me.lorenzo0111.multilang.protocol.PacketHandler;
import me.lorenzo0111.multilang.realtime.RealTimeTranslator;
import me.lorenzo0111.multilang.realtime.TranslatorConfig;
import me.lorenzo0111.multilang.storage.StorageManager;
import me.lorenzo0111.multilang.tasks.UpdateTask;
import me.lorenzo0111.multilang.utils.PluginLoader;
import me.lorenzo0111.pluginslib.audience.BukkitAudienceManager;
import me.lorenzo0111.pluginslib.database.connection.IConnectionHandler;
import me.lorenzo0111.pluginslib.database.objects.Column;
import me.lorenzo0111.pluginslib.database.objects.Table;
import me.lorenzo0111.pluginslib.dependency.DependencyManager;
import me.lorenzo0111.pluginslib.scheduler.BukkitScheduler;
import me.lorenzo0111.pluginslib.scheduler.IScheduler;
import me.lorenzo0111.pluginslib.updater.UpdateChecker;
import me.lorenzo0111.rocketplaceholders.api.IRocketPlaceholdersAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public final class MultiLangPlugin extends JavaPlugin {
    private IMultiLangAPI api;
    private IRocketPlaceholdersAPI rocketPlaceholdersAPI;
    private Hook hook;
    private TranslatorConfig translators;
    private ConfigManager configManager;
    private PluginLoader loader;
    private static MultiLangPlugin instance = null;
    private DatabaseManager databaseManager;
    private final Cache<UUID,LocalizedPlayer> playerCache = new PlayersCache();
    private File cacheFolder;
    private StorageManager storage;
    private StorageType type;
    private UpdateChecker updater;
    private PacketHandler protocol;

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
        this.loadDependencies();
    }

    @Override
    public void onEnable() {
        if (instance == null) {
            this.getLogger().warning("Detected plugin reload. Please do not use /reload.");
            this.onLoad();
        }

        BukkitAudienceManager.init(this);
        try {
            this.updateConfig();
        } catch (IOException e) {
            this.getLogger().log(Level.WARNING, "An error has occurred while trying to auto-update the configuration.",e);
        }

        this.loader = new PluginLoader(this);
        this.storage = new StorageManager(this);

        if (!loader.init()) {
            return;
        }

        this.getServer().getPluginManager().registerEvents(new JoinListener(this),this);

        Bukkit.getScheduler().runTaskTimer(this,
                new UpdateTask(this),
                60 * 20L,120 * 20L);

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.getLogger().info("Hooked with ProtocolLib. Now updating packets.");
            protocol = new PacketHandler(ProtocolLibrary.getProtocolManager(),this);
            protocol.init();
        }

        loader.commands();
        loader.api();
        loader.gui();
        loader.messages();
        loader.metrics();

        this.updater = new UpdateChecker(new BukkitScheduler(this),this.getDescription().getVersion(), this.getName(),93235,"https://www.spigotmc.org/resources/93235/",null,null);

        try {
            this.resetConnection();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        try {
            this.reloadRealtimeConfig();
            if (translators.isUseCache()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> translators.loadCache(databaseManager), 60 * 20L);
            }
        } catch (ApiException e) {
            this.getLogger().severe("An error has occurred while trying to load the realtime configuration: " + e.getMessage());
        }

        this.cacheFolder = new File(this.getDataFolder(), "cache");

        if (!cacheFolder.exists() && !cacheFolder.mkdir()) {
            this.getLogger().warning("Unable to create cache folder, dump file won't be saved.");
        }
    }

    @Override
    public void onDisable() {
        BukkitAudienceManager.shutdown();
        this.configManager.unregisterAll();
        if (protocol != null)
            protocol.unload();
        this.getLogger().info("Closing database connection..");

        try {
            this.getLogger().info("Saving data before closing connection..");
            this.translators.saveCacheSync(databaseManager);
            if (this.getDatabaseManager().getConnectionHandler() != null)
                this.getDatabaseManager().getConnectionHandler().close();
        } catch (SQLException e) {
            this.getLogger().log(Level.WARNING, "An error has occurred while closing tasks. Bad things may happen.", e);
        }

        Bukkit.getScheduler().cancelTasks(this);

        this.getLogger().info("Cleaning cache..");

        this.getPlayerCache().reset();

        if (this.cacheFolder == null) {
            return;
        }

        File[] files = this.cacheFolder.listFiles(File::isFile);

        if (files != null) {
            Arrays.stream(files)
                    .filter((file) -> file.getName().endsWith(".json"))
                    .forEach(File::delete);
        }
    }

    public void register() {
        this.getHook().register(rocketPlaceholdersAPI);
    }

    public void unregister() {
        this.getHook().unregister(rocketPlaceholdersAPI);
    }

    public Hook getHook() {
        return hook;
    }

    public void setRocketPlaceholdersAPI(IRocketPlaceholdersAPI rocketPlaceholdersAPI) {
        this.rocketPlaceholdersAPI = rocketPlaceholdersAPI;
    }

    public void setHook(Hook hook) {
        this.hook = hook;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static MultiLangPlugin getInstance() {
        return instance;
    }

    public PluginLoader getLoader() {
        return loader;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Cache<UUID, LocalizedPlayer> getPlayerCache() {
        return playerCache;
    }

    public String getConfig(String path) {
        return this.getConfig().getString(path);
    }

    public File getCacheFolder() {
       return this.cacheFolder;
    }

    public void resetConnection() throws ReloadException, SQLException, IOException {
        if (databaseManager != null && databaseManager.getConnectionHandler() != null)
            databaseManager.getConnectionHandler().close();

        this.type = StorageType.valueOf(this.getConfig("storage"));
        Objects.requireNonNull(this.type, "Invalid storage type");

        final IConnectionHandler connection = DatabaseManager.createConnection(this);
        if (connection == null) throw new ReloadException("Connection cannot be null");

        IScheduler scheduler = new BukkitScheduler(this);

        Table playersTable = new Table
                (scheduler, connection,
                        "multilang_players",
                        Arrays.asList(
                                new Column("uuid", "TEXT"),
                                new Column("locale", "TEXT")
                        ));

        Table cacheTable = new Table
                (scheduler, connection,
                        "multilang_cache",
                        Arrays.asList(
                                new Column("text", "TEXT"),
                                new Column("translations", "TEXT")
                        ));

        this.databaseManager = new DatabaseManager(this, playersTable, cacheTable, connection);
    }

    public StorageManager getStorage() {
        return storage;
    }

    public StorageType getStorageType() {
        return type;
    }

    private void loadDependencies() {
        this.getLogger().info("Loading libraries..");
        this.getLogger().info("Note: This might take a few minutes on first run.");

        try {
            DependencyManager dm = new DependencyManager(this.getName(), this.getDataFolder().toPath());
            dm.enableMirror();
            long time = dm.build();

            this.getLogger().info(String.format("Loaded libraries in %sms.", time));
        } catch (ReflectiveOperationException | URISyntaxException | NoSuchAlgorithmException | IOException e) {
            this.getLogger().severe("Unable to load dependencies...");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public void updateConfig() throws IOException {
        boolean needsSave = false;
        Object languages = this.getConfig().get("languages");

        if (languages instanceof List) {
            this.getLogger().info("Trying to auto update the configuration: languages");

            this.getConfig().set("languages",null);

            for (Object key : (List) languages) {
                this.getConfig().set("languages."+key,"Please set this to a valid key. Read the documentation for more information.");
            }

            this.getConfig().set("old.languages",languages);

            needsSave = true;

            this.getLogger().info("Configuration updated! I put some fields to complete, please read the configuration.");
        }

        if (!(this.getConfig().get("real-time", null) instanceof ConfigurationSection)) {
            this.getLogger().info("Trying to auto update the configuration: real-time");
            ConfigurationSection section = this.getConfig().createSection("real-time");
            section.set("enabled",false);
            section.set("cache",true);
            section.set("server-messages",false);
            section.set("ignore", Collections.singletonList("\\\\/"));
            section.set("key", "Get one at https://multilang.lorenzo0111.me/");
            section.set("autoupdater-comment","This section has been automatically generated by the plugin. Since the configuration api does not allow us to put comments, please read our documentation for more information. https://wiki.lorenzo0111.me/multilang");
            this.getConfig().set("real-time",section);
            needsSave = true;
        }

        if (!this.getConfig().getString("realtime.api", "realtime").equalsIgnoreCase("realtime")) {
            this.getLogger().severe("RealTime google and bing api has been deprecated. Please retrieve a new api key at https://multilang.lorenzo0111.me");
        }

        if (needsSave) {
            this.saveConfig();
        }
    }
    
    public void customDebug(@NotNull String key, @Nullable String prefix, Object message) {
        if (this.getConfig().getBoolean("debug." + key)) {
            this.getLogger().info("[Debug] [" + prefix + "] " + message);
        }
    }

    public void debug(Object message) {
        if (this.getConfig().getBoolean("debug.default")) {
            this.getLogger().info("[Debug] " + message);
        }
    }

    public UpdateChecker getUpdater() {
        return updater;
    }

    public IMultiLangAPI getApi() {
        return api;
    }

    public TranslatorConfig getTranslators() {
        return translators;
    }

    public PacketHandler getProtocol() {
        return protocol;
    }

    public void reloadRealtimeConfig() throws ApiException {
        if (translators != null && translators.isUseCache()) {
            translators.saveCache(databaseManager);
        }

        translators = new TranslatorConfig();
        translators.canRegister(true);
        translators.registerTranslator("realtime", new RealTimeTranslator(this.getConfig().getString("real-time.key")));
        translators.setInUse("realtime");
        translators.setEnabled(this.getConfig().getBoolean("real-time.enabled"));
        translators.setUseCache(this.getConfig().getBoolean("real-time.cache"));
        translators.setPatterns(this.getConfig().getStringList("real-time.ignore"));
        translators.setTranslateServer(this.getConfig().getBoolean("real-time.server-messages"));
        if (!translators.testConnection()) {
            this.getLogger().severe("RealTime API is not working. Please check your api url.");
            this.getLogger().severe("Disabling real-time translation.");
            translators.setEnabled(false);
        }
    }

    public void setApi(@NotNull IMultiLangAPI api) {
        if (this.api != null) {
            throw new IllegalStateException("API has been already initialized");
        }

        this.api = api;
    }
}
