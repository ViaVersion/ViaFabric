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
package com.viaversion.fabric.mc1152.mixin.gui.client;

import com.viaversion.fabric.mc1152.ViaFabric;
import com.viaversion.fabric.mc1152.gui.ViaConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
    protected MixinMultiplayerScreen(Text title, UnsupportedOperationException e) {
        super(title);
        throw e;
    }

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        ButtonWidget enableClientSideViaVersion = new TexturedButtonWidget(this.width / 2 + 113, 10,
                40, 20, // Size
                0, 0, // Start pos of texture
                20, // v Hover offset
                new Identifier("viafabric:textures/gui/widgets.png"),
                256, 256, // Texture size
                it -> MinecraftClient.getInstance().openScreen(new ViaConfigScreen(this)),
                new TranslatableText("gui.via_button").asString());
        if (ViaFabric.config.isHideButton()) enableClientSideViaVersion.visible = false;
        addButton(enableClientSideViaVersion);
    }
}
