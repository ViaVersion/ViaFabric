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
package com.viaversion.fabric.mc1201.mixin.debug.client;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.mc1201.ViaFabric;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.ConnectionDetails;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void sendConnectionDetails(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (!ViaFabric.config.isSendConnectionDetails()) {
            return;
        }

        @SuppressWarnings("ConstantConditions") ChannelHandler viaDecoder = ((MixinConnectionAccessor) connection).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            UserConnection connection = ((FabricDecodeHandler) viaDecoder).getInfo();

            ConnectionDetails.sendConnectionDetails(connection, ConnectionDetails.MOD_CHANNEL);
        }
    }

}
