package me.lorenzo0111.multilang.data;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.exceptions.DriverException;
import me.lorenzo0111.pluginslib.dependency.objects.Dependency;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

public enum StorageType {
    MYSQL("com.mysql.cj.jdbc.Driver"),
    FILE("me.lorenzo0111.multilang.data.StorageType");

    private final String driver;

    StorageType(String driver) {
        this.driver = driver;
    }

    @Nullable
    public CompletableFuture<Void> install(MultiLangPlugin plugin) {
        try {

            switch (this) {
                case MYSQL:
                    plugin.getDependencyManager()
                            .addDependency(new Dependency("mysql", "mysql-connector-java", "8.0.24", MultiLangPlugin.MAVEN));
                    break;
                case FILE:
                    plugin.getDependencyManager()
                            .addDependency(new Dependency("org.xerial", "sqlite-jdbc", "3.34.0", MultiLangPlugin.MAVEN));
                    break;
            }

            return plugin.getDependencyManager()
                    .installAll();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String getClassName() {
        return driver;
    }

    public Class<?> getDriver() throws DriverException {
        try {
            return Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            throw new DriverException(this);
        }
    }
}
