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
package com.viaversion.fabric.mc1194;

import com.viaversion.fabric.mc1194.gui.ViaConfigScreen;
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
