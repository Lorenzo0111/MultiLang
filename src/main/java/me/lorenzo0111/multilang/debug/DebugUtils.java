package me.lorenzo0111.multilang.debug;

import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.commands.subcommands.admin.DebugCommand;
import me.lorenzo0111.pluginslib.debugger.Debuggable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DebugUtils implements Debuggable {
    private final MultiLangPlugin plugin;
    private final DebugCommand cmd;

    public DebugUtils(DebugCommand cmd, MultiLangPlugin plugin) {
        this.plugin = plugin;
        this.cmd = cmd;
    }

    @Override
    public @Nullable Map<String, Object> getKeys() {
        Map<String,Object> map = new HashMap<>();
        String directory = "Unable to save cache.";
        try {
            directory = cmd.saveCache().getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("cache",directory);
        return map;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
