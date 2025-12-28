/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.mc1165.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerData;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class MixinOnlineServerEntry {

    @Shadow
    @Final
    private ServerData serverData;

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/renderer/texture/TextureManager;bind(Lnet/minecraft/resources/ResourceLocation;)V"))
    private void redirectPingIcon(TextureManager textureManager, ResourceLocation identifier) {
        if (identifier.equals(GuiComponent.GUI_ICONS_LOCATION) && ((ViaServerData) this.serverData).viaFabric$translating()) {
            textureManager.bind(new ResourceLocation("viafabric:textures/gui/icons.png"));
            return;
        }
        textureManager.bind(identifier);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screens/multiplayer/JoinMultiplayerScreen;setToolTip(Ljava/util/List;)V"))
    private void addServerVer(JoinMultiplayerScreen instance, List<Component> toolTip) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerData) this.serverData).viaFabric$getServerVer());
        List<Component> lines = new ArrayList<>(toolTip);
        lines.add(new TranslatableComponent("gui.ping_version.translated", proto.getName(), proto.getVersion()));
        lines.add(this.serverData.version.copy());
        instance.setToolTip(lines);
    }
}
