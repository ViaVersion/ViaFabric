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
package com.viaversion.fabric.mc1165.mixin.gui.client;

import com.viaversion.fabric.mc1165.ViaFabric;
import com.viaversion.fabric.mc1165.gui.ViaConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MixinJoinMultiplayerScreen extends Screen {
    protected MixinJoinMultiplayerScreen(Component title, UnsupportedOperationException e) {
        super(title);
        throw e;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        Button enableClientSideViaVersion = new ImageButton(this.width / 2 + 113, 10,
            40, 20, // Size
            0, 0, // Start pos of texture
            20, // v Hover offset
            new ResourceLocation("viafabric:textures/gui/widgets.png"),
            256, 256, // Texture size
            it -> Minecraft.getInstance().setScreen(new ViaConfigScreen(this)),
            new TranslatableComponent("gui.via_button"));
        if (ViaFabric.config.isHideButton()) enableClientSideViaVersion.visible = false;
        addButton(enableClientSideViaVersion);
    }
}
