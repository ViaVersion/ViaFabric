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
package com.viaversion.fabric.mc1206;

import com.viaversion.fabric.mc1206.gui.ViaConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ViaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerGui();
    }

    private void registerGui() {
        try {
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (!(screen instanceof JoinMultiplayerScreen)) return;
                Button enableClientSideViaVersion = new ImageButton(scaledWidth / 2 + 113, 10,
                        40, 20, // Size
                        new WidgetSprites(new ResourceLocation("viafabric", "widget_unfocused"), new ResourceLocation("viafabric", "widget_focused")),
                        it -> Minecraft.getInstance().setScreen(new ViaConfigScreen(screen)),
                        Component.translatable("gui.via_button"));
                if (ViaFabric.config.isHideButton()) enableClientSideViaVersion.visible = false;
                Screens.getButtons(screen).add(enableClientSideViaVersion);
            });
        } catch (NoClassDefFoundError ignored) {
            ViaFabric.JLOGGER.info("Couldn't register screen handler as Fabric Screen isn't installed");
        }
    }
}
