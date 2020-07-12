/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ViaButton extends ButtonWidget {
    // Meant to be similar to higher versions TexturedButtonWidget
    private int startU;
    private int startV;
    private int offsetHoverV;
    private Identifier texturePath;
    private Consumer<ViaButton> onClick;

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
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int u = startU;
            int v = startV;
            if (hover) {
                v += offsetHoverV;
            }
            this.drawTexture(this.x, this.y, u, v, this.width, this.height);
        }
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        super.playDownSound(soundManager);
        onClick.accept(this);
    }
}
