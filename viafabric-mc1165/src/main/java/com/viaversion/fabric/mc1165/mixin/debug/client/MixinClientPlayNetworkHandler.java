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
package com.viaversion.fabric.mc1165.mixin.debug.client;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.mc1165.ViaFabric;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.ConnectionDetails;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private ClientConnection connection;

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void sendConnectionDetails(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (!ViaFabric.config.isSendConnectionDetails()) {
            return;
        }

        @SuppressWarnings("ConstantConditions") ChannelHandler viaDecoder = ((MixinClientConnectionAccessor) connection).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            UserConnection connection = ((FabricDecodeHandler) viaDecoder).getInfo();

            ConnectionDetails.sendConnectionDetails(connection, ConnectionDetails.MOD_CHANNEL);
        }
    }

}
