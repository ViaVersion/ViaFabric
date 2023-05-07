package com.viaversion.fabric.mc114.mixin.gui.client;

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
        if (identifier.equals(DrawableHelper.GUI_ICONS_LOCATION) && ((ViaServerInfo) this.server).isViaTranslating()) {
            textureManager.bindTexture(new Identifier("viafabric:textures/gui/icons.png"));
            return;
        }
        textureManager.bindTexture(identifier);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setTooltip(Ljava/lang/String;)V"))
    private void addServerVer(MultiplayerScreen multiplayerScreen, String text) {
        ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).getViaServerVer());
        StringBuilder builder = new StringBuilder(text);
        builder.append("\n");
        builder.append((new TranslatableText("gui.ping_version.translated", proto.getName(), proto.getVersion())).asString());
        builder.append("\n");
        builder.append(this.server.version);
        multiplayerScreen.setTooltip(builder.toString());
    }
}
