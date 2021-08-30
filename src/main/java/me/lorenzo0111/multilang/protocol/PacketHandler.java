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

package me.lorenzo0111.multilang.protocol;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketListener;
import me.lorenzo0111.multilang.MultiLangPlugin;
import me.lorenzo0111.multilang.protocol.adapter.BossBarAdapter;
import me.lorenzo0111.multilang.protocol.adapter.ChatAdapter;
import me.lorenzo0111.multilang.protocol.adapter.EntityAdapter;
import me.lorenzo0111.multilang.protocol.adapter.InventoryAdapter;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PacketHandler {
    private final ProtocolManager manager;
    private final MultiLangPlugin plugin;
    private final List<PacketListener> listeners = new ArrayList<>();
    private final List<BukkitTask> tasks = new ArrayList<>();

    public PacketHandler(ProtocolManager manager, MultiLangPlugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    public void init() {
        EntityAdapter entityAdapter = new EntityAdapter(plugin);
        BukkitTask entityTask = this.schedule(entityAdapter,TimeUnit.SECONDS, 6);
        tasks.add(entityTask);

        listeners.add(new InventoryAdapter(plugin, ListenerPriority.NORMAL));
        listeners.add(new ChatAdapter(plugin, ListenerPriority.NORMAL));
        listeners.add(new BossBarAdapter(plugin, ListenerPriority.NORMAL));

        listeners.forEach(manager::addPacketListener);
    }

    public void unload() {
        listeners.forEach(manager::removePacketListener);
        tasks.forEach(BukkitTask::cancel);
    }

    private BukkitTask schedule(Runnable runnable, TimeUnit unit, long time) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        return scheduler.runTaskTimer(plugin, runnable, 0, unit.toSeconds(time) * 20);
    }

    private BukkitTask scheduleAsync(Runnable runnable, TimeUnit unit, long time) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        return this.schedule(() -> scheduler.runTaskAsynchronously(plugin, runnable),unit,time);
    }

    public ProtocolManager getManager() {
        return manager;
    }
}
