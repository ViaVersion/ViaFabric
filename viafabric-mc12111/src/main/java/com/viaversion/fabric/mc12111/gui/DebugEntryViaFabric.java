/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.mc12111.gui;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.mc12111.mixin.debug.client.MixinConnectionAccessor;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public final class DebugEntryViaFabric implements DebugScreenEntry {

    @Override
    public void display(final DebugScreenDisplayer debugScreenDisplayer, @Nullable final Level level, @Nullable final LevelChunk levelChunk, @Nullable final LevelChunk levelChunk2) {
        final ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return;
        }

        String line = "[ViaFabric] I: " + Via.getManager().getConnectionManager().getConnections().size() + " (F: " + Via.getManager().getConnectionManager().getConnectedClients().size() + ")";
        ChannelHandler viaDecoder = ((MixinConnectionAccessor) connection.getConnection()).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler fabricDecodeHandler) {
            ProtocolInfo protocol = fabricDecodeHandler.getInfo().getProtocolInfo();
            if (protocol != null) {
                ProtocolVersion serverVer = protocol.serverProtocolVersion();
                ProtocolVersion clientVer = protocol.protocolVersion();
                line += " / C: " + clientVer + " S: " + serverVer + " A: " + fabricDecodeHandler.getInfo().isActive();
            }
        }
        debugScreenDisplayer.addLine(line);
    }
}
