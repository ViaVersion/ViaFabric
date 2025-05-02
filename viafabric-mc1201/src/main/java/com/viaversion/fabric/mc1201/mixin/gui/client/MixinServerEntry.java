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
package com.viaversion.fabric.mc1201.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerData;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class MixinServerEntry {
    @Shadow
    @Final
    private ServerData server;

    @Unique
    private static final ResourceLocation viaFabric$GUI_ICONS_TEXTURES = new ResourceLocation("textures/gui/icons.png");

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void redirectPingIcon(GuiGraphics instance, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (texture.equals(viaFabric$GUI_ICONS_TEXTURES) && ((ViaServerData) this.server).viaFabric$translating()) {
            instance.blit(new ResourceLocation("viafabric:textures/gui/icons.png"), x, y, u, v, width, height, textureWidth, textureHeight);
            return;
        }
        instance.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setMultiplayerScreenTooltip(Ljava/util/List;)V"))
    private void addServerVer(JoinMultiplayerScreen multiplayerScreen, List<Component> tooltipText) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerData) this.server).viaFabric$getServerVer());
        List<Component> lines = new ArrayList<>(tooltipText);
        lines.add(Component.translatable("gui.ping_version.translated", proto.getName(), proto.getVersion()));
        lines.add(this.server.version.copy());
        multiplayerScreen.setToolTip(lines);
    }
}
