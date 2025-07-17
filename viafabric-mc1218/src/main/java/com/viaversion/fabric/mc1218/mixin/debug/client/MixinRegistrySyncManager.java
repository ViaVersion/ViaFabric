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
package com.viaversion.fabric.mc1218.mixin.debug.client;

import com.viaversion.fabric.mc1218.ViaFabric;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RegistrySyncManager.class, remap = false)
public class MixinRegistrySyncManager {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "checkRemoteRemap", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;)V", ordinal = 0), cancellable = true)
    private static void ignoreMissingRegistries(Map<ResourceLocation, Object2IntMap<ResourceLocation>> map, CallbackInfo ci) {
        if (ViaFabric.config.isIgnoreRegistrySyncErrors()) {
            LOGGER.warn("Ignoring missing registries");
            ci.cancel();
        }
    }

}
