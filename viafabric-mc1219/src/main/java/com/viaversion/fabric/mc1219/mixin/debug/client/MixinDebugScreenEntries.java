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
package com.viaversion.fabric.mc1219.mixin.debug.client;

import com.viaversion.fabric.mc1219.gui.DebugEntryViaFabric;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;
import java.util.Map;

@Mixin(DebugScreenEntries.class)
public abstract class MixinDebugScreenEntries {

    @Mutable
    @Shadow
    @Final
    public static Map<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> PROFILES;

    @Shadow
    private static ResourceLocation register(final String string, final DebugScreenEntry debugScreenEntry) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addViaFabricEntry(CallbackInfo ci) {
        final ResourceLocation entryId = register("viafabric", new DebugEntryViaFabric());
        final Map<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> profiles = new HashMap<>();
        for (Map.Entry<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> entry : PROFILES.entrySet()) {
            final Map<ResourceLocation, DebugScreenEntryStatus> entries = new HashMap<>(entry.getValue());
            if (entry.getKey() == DebugScreenProfile.DEFAULT) {
                entries.put(entryId, DebugScreenEntryStatus.IN_F3);
            }
            profiles.put(entry.getKey(), entries);
        }
        PROFILES = profiles;
    }

}
