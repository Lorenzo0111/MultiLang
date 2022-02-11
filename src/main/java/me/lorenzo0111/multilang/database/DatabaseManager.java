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

package me.lorenzo0111.multilang.database;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.api.objects.LocalizedString;
import me.lorenzo0111.multilang.exceptions.DriverException;
import me.lorenzo0111.pluginslib.database.connection.HikariConnection;
import me.lorenzo0111.pluginslib.database.connection.IConnectionHandler;
import me.lorenzo0111.pluginslib.database.connection.SQLiteConnection;
import me.lorenzo0111.pluginslib.database.objects.Table;
import me.lorenzo0111.pluginslib.database.query.Queries;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class DatabaseManager {
    private final MultiLangPlugin plugin;
    private final Table usersTable;
    private final Table cacheTable;
    private final IConnectionHandler connectionHandler;
    private final Gson gson = new Gson();

    @Nullable
    public static IConnectionHandler createConnection(@NotNull MultiLangPlugin plugin) throws SQLException, IOException {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);

        Class<?> driver;

        try {
            driver = plugin.getStorageType()
                    .getDriver();
        } catch(DriverException ex) {
            plugin.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }

        plugin.getLogger().info("Driver found: " + driver.getName());

        switch (plugin.getStorageType()) {
            case FILE:
                return new SQLiteConnection(plugin.getDataFolder().toPath());
            case MYSQL:
                config.setPoolName("MultiLang MySQL Connection Pool");
                config.setDriverClassName(driver.getName());
                config.setJdbcUrl("jdbc:mysql://" + plugin.getConfig("mysql.ip") + ":" + plugin.getConfig("mysql.port") + "/" + plugin.getConfig("mysql.database"));
                config.addDataSourceProperty("serverName", plugin.getConfig("mysql.ip"));
                config.addDataSourceProperty("port", plugin.getConfig("mysql.port"));
                config.addDataSourceProperty("databaseName", plugin.getConfig("mysql.database"));
                config.addDataSourceProperty("user", plugin.getConfig("mysql.username"));
                config.addDataSourceProperty("password", plugin.getConfig("mysql.password"));
                config.addDataSourceProperty("useSSL", plugin.getConfig("mysql.ssl"));
                break;
        }

        try {
            return new HikariConnection(new HikariDataSource(config));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to connect to the database",e);
            return null;
        }
    }

    public DatabaseManager(MultiLangPlugin plugin, Table usersTable, Table cacheTable) throws SQLException, IOException {
        this(plugin,usersTable,cacheTable,createConnection(plugin));
    }

    public DatabaseManager(MultiLangPlugin plugin, Table usersTable, Table cacheTable, IConnectionHandler connection) {
        this.plugin = plugin;
        this.usersTable = usersTable;
        this.cacheTable = cacheTable;

        this.connectionHandler = connection;

        this.usersTable.create();
        this.cacheTable.create();
    }

    public void updateUser(LocalizedPlayer player) {
        this.usersTable.removeWhere("uuid", player)
                .thenAccept((integer) -> this.usersTable.add(player));
    }

    public CompletableFuture<LocalizedPlayer> searchPlayer(Player player) {
        final CompletableFuture<LocalizedPlayer> future = new CompletableFuture<>();

        this.usersTable.find("uuid", player.getUniqueId().toString()).thenAccept((set) -> {
            try {
                if (set.next()) {
                    future.complete(new LocalizedPlayer(player,new Locale(set.getString("locale"), plugin.getConfigManager().getLocales().get(set.getString("locale")))));
                }

                future.complete(null);
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void updateCacheTable(Map<String, LocalizedString> data) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateCacheTableSync(data);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updateCacheTableSync(Map<String, LocalizedString> data) {
        try {
            Statement statement = cacheTable.getConnection().createStatement();
            statement.executeUpdate(Queries.builder().query(Queries.CLEAR).table(cacheTable.getName()).build());
            statement.close();

            String query = Queries.builder()
                    .query(Queries.INSERT_START)
                    .table(cacheTable.getName())
                    .build() + "`text`, `translations`) VALUES(?,?);";

            PreparedStatement preparedStatement = cacheTable.getConnection().prepareStatement(query);
            for (Map.Entry<String, LocalizedString> entry : data.entrySet()) {
                preparedStatement.setString(1, entry.getKey());
                preparedStatement.setString(2, gson.toJson(entry.getValue()));
                preparedStatement.executeUpdate();
            }

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public @NotNull CompletableFuture<Map<String,LocalizedString>> getCache() {
        CompletableFuture<Map<String,LocalizedString>> future = new CompletableFuture<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet result = cacheTable.all().join();

                    Map<String,LocalizedString> data = new HashMap<>();
                    while (result.next()) {
                        data.put(result.getString("text"), gson.fromJson(result.getString("translations"), LocalizedString.class));
                    }

                    result.close();
                    future.complete(data);
                } catch (SQLException e) {
                    future.completeExceptionally(e);
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    @Nullable
    public Connection getConnection() throws SQLException {
        return connectionHandler.getConnection();
    }

    @Nullable
    public IConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Table getUsersTable() {
        return usersTable;
    }

    public Table getCacheTable() {
        return cacheTable;
    }
}
