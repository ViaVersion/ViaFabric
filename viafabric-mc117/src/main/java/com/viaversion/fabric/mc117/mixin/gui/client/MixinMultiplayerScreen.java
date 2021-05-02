package com.viaversion.fabric.mc117.mixin.gui.client;

import com.viaversion.fabric.mc117.ViaFabric;
import com.viaversion.fabric.mc117.gui.ViaConfigScreen;
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

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        ButtonWidget enableClientSideViaVersion = new TexturedButtonWidget(this.width / 2 + 113, 10,
                40, 20, // Size
                0, 0, // Start pos of texture
                20, // v Hover offset
                new Identifier("viafabric:textures/gui/widgets.png"),
                256, 256, // Texture size
                it -> MinecraftClient.getInstance().openScreen(new ViaConfigScreen(this)),
                new TranslatableText("gui.via_button"));
        if (ViaFabric.config.isHideButton()) enableClientSideViaVersion.visible = false;
        addButton(enableClientSideViaVersion);
    }
}
