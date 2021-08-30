package me.lorenzo0111.multilang.protocol.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.lorenzo0111.multilang.MultiLangPlugin;
import org.jetbrains.annotations.NotNull;

public class BossBarAdapter extends BaseAdapter {

    public BossBarAdapter(MultiLangPlugin plugin, ListenerPriority listenerPriority) {
        super(plugin, listenerPriority, PacketType.Play.Server.BOSS);
    }

    @Override
    public void onPacketSending(@NotNull PacketEvent event) {
        PacketContainer packet = event.getPacket();

        WrappedChatComponent component;

        if (MinecraftVersion.CAVES_CLIFFS_1.atOrAbove()) {
            component = packet.getStructures().read(1).getChatComponents().read(0);
        } else {
            component = packet.getChatComponents().read(0);
        }

        this.handle(event.getPlayer(),component, () -> {
            if (MinecraftVersion.CAVES_CLIFFS_1.atOrAbove()) {
                packet.getStructures().read(1).getChatComponents().write(0, component);
            } else {
                packet.getChatComponents().write(0, component);
            }
        });
    }
}
