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
package com.viaversion.fabric.mc1206.mixin.gui.client;

import com.google.common.collect.Lists;
import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public class MixinServerEntry {
    @Shadow
    @Final
    private ServerInfo server;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    private Identifier redirectPingIcon(Identifier texture) {
        if (((ViaServerInfo) this.server).viaFabric$translating() && texture.getPath().startsWith("server_list/ping")) {
            return new Identifier("viafabric", texture.getPath());
        }
        return texture;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Lnet/minecraft/text/Text;)V"))
    private void addServerVer(MultiplayerScreen instance, Text text) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).viaFabric$getServerVer());
        List<Text> lines = new ArrayList<>();
        lines.add(text);
        lines.add(Text.translatable("gui.ping_version.translated", proto.getName(), proto.getVersion()));
        instance.setTooltip(Lists.transform(lines, Text::asOrderedText));
    }
}
