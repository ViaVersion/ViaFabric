/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.mixin.debug.client;

import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.ConnectionDetails;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {

    @Shadow
    public abstract Connection getConnection();

    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void sendConnectionDetails(ClientboundLoginPacket packet, CallbackInfo ci) {
        @SuppressWarnings("ConstantConditions") ChannelHandler viaDecoder = ((MixinConnectionAccessor) getConnection()).getChannel().pipeline().get(ViaDecodeHandler.NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            UserConnection connection = ((FabricDecodeHandler) viaDecoder).connection();

            ConnectionDetails.sendConnectionDetails(connection, ConnectionDetails.MOD_CHANNEL);
        }
    }

}
