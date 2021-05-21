package com.viaversion.fabric.mc117.mixin.gui.client;

import com.viaversion.fabric.common.gui.ViaServerInfo;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.fabric.mc117.mixin.debug.client.MixinClientConnectionAccessor;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1")
public abstract class MixinMultiplayerServerListPingerListener implements ClientQueryPacketListener {
    @Shadow
    public abstract ClientConnection getConnection();

    @Redirect(method = "onResponse(Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerInfo;getIcon()Ljava/lang/String;"))
    private String onResponseCaptureServerInfo(ServerInfo serverInfo) {
        FabricDecodeHandler decoder = ((MixinClientConnectionAccessor) this.getConnection()).getChannel()
                .pipeline().get(FabricDecodeHandler.class);
        if (decoder != null) {
            ((ViaServerInfo) serverInfo).setViaTranslating(decoder.getInfo().isActive());
            ((ViaServerInfo) serverInfo).setViaServerVer(decoder.getInfo().getProtocolInfo().getServerProtocolVersion());
        }
        return serverInfo.getIcon();
    }
}
