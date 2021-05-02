package com.viaversion.fabric.mc18.gui;

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
