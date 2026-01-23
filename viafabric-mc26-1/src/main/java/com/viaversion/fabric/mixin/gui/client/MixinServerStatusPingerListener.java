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
package com.viaversion.fabric.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerData;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.mixin.debug.client.MixinConnectionAccessor;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.multiplayer.ServerStatusPinger$1")
public abstract class MixinServerStatusPingerListener implements ClientStatusPacketListener {

    @Shadow
    @Final
    ServerData val$data;

    @Shadow
    @Final
    Connection val$connection;

    @Inject(method = "handleStatusResponse", at = @At(value = "HEAD"))
    private void onStatusResponseCaptureServerInfo(ClientboundStatusResponsePacket clientboundStatusResponsePacket, CallbackInfo ci) {
        FabricDecodeHandler decoder = ((MixinConnectionAccessor) this.val$connection).getChannel()
            .pipeline().get(FabricDecodeHandler.class);
        if (decoder != null) {
            ((ViaServerData) this.val$data).viaFabric$setTranslating(decoder.connection().isActive());
            ((ViaServerData) this.val$data).viaFabric$setServerVer(decoder.connection().getProtocolInfo().getServerProtocolVersion());
        }
    }
}
