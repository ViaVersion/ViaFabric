/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
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
package com.viaversion.fabric.mc1201.mixin.debug.client;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
    @Inject(at = @At("RETURN"), method = "getLeftText")
    protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
        String line = "[ViaFabric] I: " + Via.getManager().getConnectionManager().getConnections().size() + " (F: "
                + Via.getManager().getConnectionManager().getConnectedClients().size() + ")";
        @SuppressWarnings("ConstantConditions") ChannelHandler viaDecoder = ((MixinClientConnectionAccessor) MinecraftClient.getInstance().getNetworkHandler()
                .getConnection()).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            ProtocolInfo protocol = ((FabricDecodeHandler) viaDecoder).getInfo().getProtocolInfo();
            if (protocol != null) {
                ProtocolVersion serverVer = ProtocolVersion.getProtocol(protocol.getServerProtocolVersion());
                ProtocolVersion clientVer = ProtocolVersion.getProtocol(protocol.getProtocolVersion());
                line += " / C: " + clientVer + " S: " + serverVer + " A: " + protocol.getUser().isActive();
            }
        }

        info.getReturnValue().add(line);
    }
}
