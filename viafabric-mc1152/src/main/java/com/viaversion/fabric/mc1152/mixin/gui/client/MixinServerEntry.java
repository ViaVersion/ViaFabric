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
package com.viaversion.fabric.mc1152.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinServerEntry {
    @Shadow
    @Final
    private ServerInfo server;

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V"))
    private void redirectPingIcon(TextureManager textureManager, Identifier identifier) {
        if (identifier.equals(DrawableHelper.GUI_ICONS_LOCATION) && ((ViaServerInfo) this.server).viaFabric$translating()) {
            textureManager.bindTexture(new Identifier("viafabric:textures/gui/icons.png"));
            return;
        }
        textureManager.bindTexture(identifier);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Ljava/lang/String;)V"))
    private void addServerVer(MultiplayerScreen multiplayerScreen, String text) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).viaFabric$getServerVer());
        StringBuilder builder = new StringBuilder(text);
        builder.append("\n");
        builder.append((new TranslatableText("gui.ping_version.translated", proto.getName(), proto.getVersion())).asString());
        builder.append("\n");
        builder.append(this.server.version);
        multiplayerScreen.setTooltip(builder.toString());
    }
}
