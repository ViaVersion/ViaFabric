package com.viaversion.fabric.mc119.mixin.gui.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
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

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"))
    private void redirectPingIcon(int i, Identifier identifier) {
        if (identifier.equals(DrawableHelper.GUI_ICONS_TEXTURE) && ((ViaServerInfo) this.server).isViaTranslating()) {
            RenderSystem.setShaderTexture(i, new Identifier("viafabric:textures/gui/icons.png"));
            return;
        }
        RenderSystem.setShaderTexture(i, identifier);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setMultiplayerScreenTooltip(Ljava/util/List;)V"))
    private void addServerVer(MultiplayerScreen multiplayerScreen, List<Text> tooltipText) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).getViaServerVer());
        List<Text> lines = new ArrayList<>(tooltipText);
        lines.add(Text.translatable("gui.ping_version.translated", proto.getName(), this.server.protocolVersion));
        lines.add(this.server.version.copy());
        multiplayerScreen.setMultiplayerScreenTooltip(lines);
    }
}