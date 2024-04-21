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
package com.viaversion.fabric.mc1171.mixin.gui.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinServerEntry {
    @Shadow
    @Final
    private ServerInfo server;

    // todo fix this intermediary
    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/class_2960;)V"))
    private void redirectPingIcon(int i, Identifier identifier) {
        if (identifier.equals(DrawableHelper.GUI_ICONS_TEXTURE) && ((ViaServerInfo) this.server).viaFabric$translating()) {
            RenderSystem.setShaderTexture(i, new Identifier("viafabric:textures/gui/icons.png"));
            return;
        }
        RenderSystem.setShaderTexture(i, identifier);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Ljava/util/List;)V"))
    private void addServerVer(MultiplayerScreen multiplayerScreen, List<Text> tooltipText) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).viaFabric$getServerVer());
        List<Text> lines = new ArrayList<>(tooltipText);
        lines.add(new TranslatableText("gui.ping_version.translated", proto.getName(), proto.getVersion()));
        lines.add(this.server.version.copy());
        multiplayerScreen.setTooltip(lines);
    }
}