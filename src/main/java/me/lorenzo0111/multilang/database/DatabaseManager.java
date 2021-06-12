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

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.api.objects.Locale;
import me.lorenzo0111.multilang.api.objects.LocalizedPlayer;
import me.lorenzo0111.multilang.exceptions.DriverException;
import me.lorenzo0111.pluginslib.database.objects.Table;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class DatabaseManager {
    private final Connection connection;
    private final List<Table> tables;
    private final MultiLangPlugin plugin;
    private final Table usersTable;

    @Nullable
    public static Connection createConnection(MultiLangPlugin plugin) {
        CompletableFuture<Void> completable = plugin.getStorageType()
                .install(plugin);

        Objects.requireNonNull(completable);

        completable.thenRun(() -> {
            try {
                plugin.getStorageType()
                        .getDriver();
            } catch(DriverException ex) {
                plugin.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            }
        });

        String jdbc = null;

        try {
            switch (plugin.getStorageType()) {
                case FILE:
                    jdbc = "jdbc:sqlite:" + new File(plugin.getDataFolder(), "database.db").getAbsolutePath();
                    break;
                case MYSQL:
                    jdbc = plugin.getConfig().getString("mysql.jdbc");

                    if (jdbc == null) {
                        return DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.ip") + ":" + plugin.getConfig().getInt("mysql.port") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
                    }
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Objects.requireNonNull(jdbc);

        try {
            return DriverManager.getConnection(jdbc);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public DatabaseManager(MultiLangPlugin plugin, List<Table> tables) {
        this(plugin,tables,createConnection(plugin));
    }

    public DatabaseManager(MultiLangPlugin plugin, List<Table> tables, Connection connection) {
        this.plugin = plugin;
        this.tables = tables;

        this.connection = connection;

        tables.forEach(Table::create);

        this.usersTable = tables.stream()
                .filter((item) -> item.getName().equals("players"))
                .findFirst()
                .orElse(null);
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
                    future.complete(new LocalizedPlayer(player,new Locale(set.getString("locale"))));
                }

                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public void updateTable(String table, Collection<LocalizedPlayer> items) {
        new BukkitRunnable() {
            @Override
            public void run() {

                final Optional<Table> first = tables.stream().filter((tableItem) -> tableItem.getName().equals(table)).findFirst();
                if (!first.isPresent()) {
                    return;
                }

                final Table tableItem = first.get();
                tableItem.clear();
                items.forEach(tableItem::add);

            }
        }.runTaskAsynchronously(plugin);
    }

    public Connection getConnection() {
        return connection;
    }

    public Table getUsersTable() {
        return usersTable;
    }
}
