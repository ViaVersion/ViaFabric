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
package com.viaversion.fabric.mc1204.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.mc1204.mixin.debug.client.MixinClientConnectionAccessor;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1")
public abstract class MixinMultiplayerServerListPingerListener implements ClientQueryPacketListener {
    @Accessor
    abstract ClientConnection getField_3774(); // Synthetic

    @Accessor
    abstract ServerInfo getField_3776(); // Synthetic

    @Inject(method = "onResponse(Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;)V", at = @At(value = "HEAD"))
    private void onResponseCaptureServerInfo(QueryResponseS2CPacket packet, CallbackInfo ci) {
        FabricDecodeHandler decoder = ((MixinClientConnectionAccessor) this.getField_3774()).getChannel()
                .pipeline().get(FabricDecodeHandler.class);
        if (decoder != null) {
            ((ViaServerInfo) getField_3776()).viaFabric$setTranslating(decoder.getInfo().isActive());
            ((ViaServerInfo) getField_3776()).viaFabric$setServerVer(decoder.getInfo().getProtocolInfo().getServerProtocolVersion());
        }
    }
}
