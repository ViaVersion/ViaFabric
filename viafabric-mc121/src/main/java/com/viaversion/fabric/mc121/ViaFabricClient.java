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
package com.viaversion.fabric.mc121;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.common.util.ArmorHudEmulation;
import com.viaversion.fabric.mc121.gui.ViaConfigScreen;
import com.viaversion.fabric.mc121.mixin.debug.client.MixinClientConnectionAccessor;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.v1_8to1_9.data.ArmorTypes1_8;
import io.netty.channel.ChannelHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ViaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerGui();
        registerArmorHud1_8();
    }

    private void registerGui() {
        try {
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (!(screen instanceof MultiplayerScreen)) return;
                ButtonWidget enableClientSideViaVersion = new TexturedButtonWidget(scaledWidth / 2 + 113, 10,
                        40, 20, // Size
                        new ButtonTextures(Identifier.of("viafabric", "widget_unfocused"), Identifier.of("viafabric", "widget_focused")),
                        it -> MinecraftClient.getInstance().setScreen(new ViaConfigScreen(screen)),
                        Text.translatable("gui.via_button"));
                if (ViaFabric.config.isHideButton()) enableClientSideViaVersion.visible = false;
                Screens.getButtons(screen).add(enableClientSideViaVersion);
            });
        } catch (NoClassDefFoundError ignored) {
            ViaFabric.JLOGGER.info("Couldn't register screen handler as Fabric Screen isn't installed");
        }
    }

    private void registerArmorHud1_8() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!ViaFabric.config.isShowArmorHud()) {
                return;
            }
            final UserConnection connection = getUserConnection();
            if (connection.getProtocolInfo().protocolVersion().newerThan(ProtocolVersion.v1_8)) {
                return;
            }
            int armor = 0;
            for (final ItemStack stack : MinecraftClient.getInstance().player.getInventory().armor) {
                armor += ArmorTypes1_8.findByType(Registries.ITEM.getId(stack.getItem()).toString()).getArmorPoints();
            }
            ArmorHudEmulation.sendArmorUpdate(connection, MinecraftClient.getInstance().player.getId(), armor);
        });
    }

    public static UserConnection getUserConnection() {
        ClientConnection connection = MinecraftClient.getInstance().getNetworkHandler().getConnection();
        if (connection == null) {
            return null;
        }
        ChannelHandler viaDecoder = ((MixinClientConnectionAccessor) connection).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            return ((FabricDecodeHandler) viaDecoder).getInfo();
        }
        return null;
    }
}
