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
package com.viaversion.fabric.mc1218.mixin.pipeline.client;

import com.viaversion.fabric.mc1218.ViaFabric;
import com.viaversion.fabric.mc1218.service.ProtocolAutoDetector;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.minecraft.network.Connection;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Connection.class)
public class MixinConnection {
    @Inject(method = "connectToServer", at = @At("HEAD"))
    private static void onConnectToServer(InetSocketAddress address, boolean useEpollIfAvailable, LocalSampleLogger sampleLogger, CallbackInfoReturnable<Connection> cir) {
        try {
            if (!ViaFabric.config.isClientSideEnabled()) return;
            ProtocolAutoDetector.detectVersion(address).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            ViaFabric.JLOGGER.log(Level.WARNING, "Could not auto-detect protocol for " + address + " " + e);
        }
    }
}
