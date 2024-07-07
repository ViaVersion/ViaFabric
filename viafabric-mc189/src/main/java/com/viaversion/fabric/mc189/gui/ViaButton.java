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
package com.viaversion.fabric.mc189.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ViaButton extends ButtonWidget {
    // Meant to be similar to higher versions TexturedButtonWidget
    private final int startU;
    private final int startV;
    private final int offsetHoverV;
    private final Identifier texturePath;
    private final Consumer<ViaButton> onClick;

    public ViaButton(int id, int x, int y, int width, int height, int startU, int startV, int offsetHoverV, Identifier texturePath,
                     int textureSizeX, int textureSizeY, Consumer<ViaButton> onClick, String altTxt) {
        super(id, x, y, width, height, altTxt);
        this.startU = startU;
        this.startV = startV;
        this.offsetHoverV = offsetHoverV;
        this.texturePath = texturePath;
        assert textureSizeX == 256;
        assert textureSizeY == 256;
        this.onClick = onClick;
    }

    public void render(MinecraftClient client, int mouseX, int mouseY) {
        // Modified copy-paste from LockButtonWidget
        if (this.visible) {
            client.getTextureManager().bindTexture(texturePath);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int v = startV;
            if (hover) {
                v += offsetHoverV;
            }
            this.drawTexture(this.x, this.y, startU, v, this.width, this.height);
        }
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        super.playDownSound(soundManager);
        onClick.accept(this);
    }
}
