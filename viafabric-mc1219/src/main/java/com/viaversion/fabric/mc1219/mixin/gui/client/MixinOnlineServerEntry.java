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
package com.viaversion.fabric.mc1219.mixin.gui.client;

import com.google.common.collect.Lists;
import com.viaversion.fabric.common.gui.ViaServerData;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class MixinOnlineServerEntry {

    @Shadow
    @Final
    private ServerData serverData;

    @ModifyArg(method = "renderContent", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private ResourceLocation redirectPingIcon(ResourceLocation texture) {
        if (((ViaServerData) this.serverData).viaFabric$translating() && texture.getPath().startsWith("server_list/ping")) {
            return ResourceLocation.tryBuild("viafabric", texture.getPath());
        }
        return texture;
    }

    @Redirect(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/network/chat/Component;II)V"))
    private void addServerVer(GuiGraphics instance, Component component, int x, int y) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerData) this.serverData).viaFabric$getServerVer());
        List<Component> lines = new ArrayList<>();
        lines.add(component);
        lines.add(Component.translatable("gui.ping_version.translated", proto.getName(), proto.getVersion()));
        instance.setTooltipForNextFrame(Lists.transform(lines, Component::getVisualOrderText), x, y);
    }
}
