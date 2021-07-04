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

package me.lorenzo0111.multilang.protocol.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.protocol.PacketUtils;
import me.lorenzo0111.multilang.utils.RegexChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityAdapter implements Runnable {
    private final MultiLangPlugin plugin;
    private static EntityAdapter instance;

    public EntityAdapter(MultiLangPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    @Override
    public void run() {
        Multimap<Player, Entity> map = LinkedHashMultimap.create();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final List<Entity> entities = player.getNearbyEntities(50, 50, 50)
                    .stream()
                    .filter(entity -> entity.getCustomName() != null)
                    .collect(Collectors.toList());

            map.putAll(player, entities);
        }

        if (plugin.getServer().isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> this.runTask(map));
            return;
        }

        this.runTask(map);
    }

    public void runTask(Multimap<Player,Entity> map) {
        plugin.customDebug("tasks","Update Task", String.format("Starting to process %s entities..", map.values().size()));

        for (Map.Entry<Player, Entity> entry : map.entries()) {
            Player player = entry.getKey();
            Entity entity = entry.getValue();

            // Check if player is valid
            if (!player.isOnline()) continue;
            if (!player.isValid()) continue;

            // Check if entity is valid
            if (!entity.isValid()) continue;
            if (!(entity instanceof LivingEntity)) continue;


            String customName = entity.getCustomName();
            if (customName == null) continue;
            if (customName.isEmpty()) continue;

            String newName = RegexChecker.replace(player, customName);

            try {
                WrappedDataWatcher watcher = PacketUtils.renameEntity(entity,newName);

                PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
                packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
                packet.getIntegers().write(0, entity.getEntityId());

                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                plugin.getLogger().warning("Unable to rename entity " + entity.getEntityId() + ": " + e.getMessage());
            }


        }
    }

    public static EntityAdapter getInstance() {
        return instance;
    }
}
