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

import com.zaxxer.hikari.HikariDataSource;
import me.lorenzo0111.multilang.api.objects.Cache;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.cache.PlayersCache;
import me.lorenzo0111.multilang.data.StorageType;
import me.lorenzo0111.multilang.database.DatabaseManager;
import me.lorenzo0111.multilang.exceptions.ReloadException;
import me.lorenzo0111.multilang.handlers.ConfigManager;
import me.lorenzo0111.multilang.listeners.JoinListener;
import me.lorenzo0111.multilang.storage.StorageManager;
import me.lorenzo0111.multilang.tasks.UpdateTask;
import me.lorenzo0111.multilang.utils.PluginLoader;
import me.lorenzo0111.pluginslib.database.connection.HikariConnection;
import me.lorenzo0111.pluginslib.database.objects.Column;
import me.lorenzo0111.pluginslib.database.objects.Table;
import me.lorenzo0111.pluginslib.dependency.beta.SlimJarDependencyManager;
import me.lorenzo0111.pluginslib.updater.UpdateChecker;
import me.lorenzo0111.rocketplaceholders.api.RocketPlaceholdersAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public final class MultiLangPlugin extends JavaPlugin {
    private RocketPlaceholdersAPI rocketPlaceholdersAPI;
    private ConfigManager configManager;
    private PluginLoader loader;
    private static MultiLangPlugin instance;
    private DatabaseManager databaseManager;
    private final Cache<UUID,LocalizedPlayer> playerCache = new PlayersCache();
    private File cacheFolder;
    private StorageManager storage;
    private StorageType type;
    private UpdateChecker updater;

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
        this.loadDependencies();
    }

    @Override
    public void onEnable() {
        this.loadPlugin();
    }

    private void loadPlugin() {
        this.loader = new PluginLoader(this);
        this.storage = new StorageManager(this);

        if (!loader.init()) {
            return;
        }

        this.getServer().getPluginManager().registerEvents(new JoinListener(this),this);

        Bukkit.getScheduler().runTaskTimer(this,
                new UpdateTask(this),
                60 * 20L,120 * 20L);

        loader.commands();
        loader.api();
        loader.gui();
        loader.messages();
        loader.metrics();

        this.updater = new UpdateChecker(this,93235,"https://www.spigotmc.org/resources/93235/",null);

        try {
            this.resetConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.cacheFolder = new File(this.getDataFolder(), "cache");

        if (!cacheFolder.exists() && !cacheFolder.mkdir()) {
            this.getLogger().warning("Unable to create cache folder, dump file won't saved.");
        }
    }

    @Override
    public void onDisable() {
        this.configManager.unregisterAll();
        Bukkit.getScheduler().cancelTasks(this);
        this.getLogger().info("Closing database connection..");

        if (this.getDatabaseManager().getHikari() != null)
            this.getDatabaseManager().getHikari().close();

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

    public RocketPlaceholdersAPI getRocketPlaceholdersAPI() {
        return rocketPlaceholdersAPI;
    }

    public void setRocketPlaceholdersAPI(RocketPlaceholdersAPI rocketPlaceholdersAPI) {
        this.rocketPlaceholdersAPI = rocketPlaceholdersAPI;
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

    public void resetConnection() throws ReloadException, SQLException {
        if (databaseManager != null && databaseManager.getHikari() != null && !databaseManager.getHikari().isClosed())
            databaseManager.getHikari().close();

        this.type = StorageType.valueOf(this.getConfig("storage"));
        Objects.requireNonNull(this.type, "Invalid storage type");

        final HikariDataSource connection = DatabaseManager.createConnection(this);
        if (connection == null) throw new ReloadException("Connection cannot be null");

        Table playersTable = new Table
                (this,
                        new HikariConnection(connection),
                        "players",
                        Arrays.asList(
                                new Column("uuid", "STRING"),
                                new Column("locale", "STRING")
                        ));

        this.databaseManager = new DatabaseManager(this, Collections.singletonList(playersTable), connection);
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
            long time = new SlimJarDependencyManager(this)
                    .build();
            this.getLogger().info(String.format("Loaded libraries in %sms.", time));
        } catch (ReflectiveOperationException | URISyntaxException | NoSuchAlgorithmException | IOException e) {
            this.getLogger().severe("Unable to load dependencies...");
            e.printStackTrace();
        }
    }

    public UpdateChecker getUpdater() {
        return updater;
    }
}
