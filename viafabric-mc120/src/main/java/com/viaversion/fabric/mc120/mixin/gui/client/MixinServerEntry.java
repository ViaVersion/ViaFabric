package com.viaversion.fabric.mc120.mixin.gui.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.DrawContext;
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
    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void redirectPingIcon(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (texture.equals(GUI_ICONS_TEXTURES) && ((ViaServerInfo) this.server).isViaTranslating()) {
            instance.drawTexture(new Identifier("viafabric:textures/gui/icons.png"), x, y, u, v, width, height, textureWidth, textureHeight);
            return;
        }
        instance.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    private static final Identifier GUI_ICONS_TEXTURES = new Identifier("textures/gui/icons.png");

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setMultiplayerScreenTooltip(Ljava/util/List;)V"))
    private void addServerVer(MultiplayerScreen multiplayerScreen, List<Text> tooltipText) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).getViaServerVer());
        List<Text> lines = new ArrayList<>(tooltipText);
        lines.add(Text.translatable("gui.ping_version.translated", proto.getName(), proto.getVersion()));
        multiplayerScreen.setMultiplayerScreenTooltip(lines);
    }
}
