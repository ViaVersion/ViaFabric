package com.viaversion.fabric.mc120;

import com.viaversion.fabric.mc120.gui.ViaConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ViaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerGui();
    }

    private void registerGui() {
        try {
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (!(screen instanceof MultiplayerScreen)) return;
                ButtonWidget enableClientSideViaVersion = new TexturedButtonWidget(scaledWidth / 2 + 113, 10,
                        40, 20, // Size
                        0, 0, // Start pos of texture
                        20, // v Hover offset
                        new Identifier("viafabric:textures/gui/widgets.png"),
                        256, 256, // Texture size
                        it -> MinecraftClient.getInstance().setScreen(new ViaConfigScreen(screen)),
                        Text.translatable("gui.via_button"));
                if (ViaFabric.config.isHideButton()) enableClientSideViaVersion.visible = false;
                Screens.getButtons(screen).add(enableClientSideViaVersion);
            });
        } catch (NoClassDefFoundError ignored) {
            ViaFabric.JLOGGER.info("Couldn't register screen handler as Fabric Screen isn't installed");
        }
    }
}
