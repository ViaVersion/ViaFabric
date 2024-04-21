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
package com.viaversion.fabric.mc1194.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerInfo;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements ViaServerInfo {
    @Unique
    private boolean viaFabric$translating;

    @Unique
    private int viaFabric$serverVer;

    @Override
    public int viaFabric$getServerVer() {
        return viaFabric$serverVer;
    }

    @Override
    public void viaFabric$setServerVer(int ver) {
        this.viaFabric$serverVer = ver;
    }

    @Override
    public boolean viaFabric$translating() {
        return viaFabric$translating;
    }

    @Override
    public void viaFabric$setTranslating(boolean via) {
        this.viaFabric$translating = via;
    }
}
