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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.Optional;

public final class PacketUtils {
    private static final int CUSTOM_NAME_INDEX = 2;

    public static PacketContainer renameEntity(Entity entity, String name) {
        WrappedDataWatcher.Serializer serializer;
        Object value;

        // Before 1.13 the custom name is a plain string and not a chat component
        if (MinecraftVersion.AQUATIC_UPDATE.atOrAbove()) {
            serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            value = Optional.of(WrappedChatComponent.fromChatMessage(name)[0].getHandle());
        } else {
            serializer = WrappedDataWatcher.Registry.get(String.class);
            value = name;
        }

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entity.getEntityId());

        // Only the custom name is sent, so that the other metadata (like the name visibility) is left untouched
        if (MinecraftVersion.FEATURE_PREVIEW_UPDATE.atOrAbove()) {
            packet.getDataValueCollectionModifier().write(0, Collections.singletonList(new WrappedDataValue(CUSTOM_NAME_INDEX, serializer, value)));
        } else {
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serializer), value);
            packet.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        }

        return packet;
    }
}
