package com.viaversion.fabric.mc120.mixin.debug.client;

import com.viaversion.fabric.common.handler.CommonTransformer;
import com.viaversion.fabric.common.handler.FabricDecodeHandler;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
    @Inject(at = @At("RETURN"), method = "getLeftText")
    protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
        String line = "[ViaFabric] I: " + Via.getManager().getConnectionManager().getConnections().size() + " (F: "
                + Via.getManager().getConnectionManager().getConnectedClients().size() + ")";
        @SuppressWarnings("ConstantConditions") ChannelHandler viaDecoder = ((MixinClientConnectionAccessor) MinecraftClient.getInstance().getNetworkHandler()
                .getConnection()).getChannel().pipeline().get(CommonTransformer.HANDLER_DECODER_NAME);
        if (viaDecoder instanceof FabricDecodeHandler) {
            ProtocolInfo protocol = ((FabricDecodeHandler) viaDecoder).getInfo().getProtocolInfo();
            if (protocol != null) {
                ProtocolVersion serverVer = ProtocolVersion.getProtocol(protocol.getServerProtocolVersion());
                ProtocolVersion clientVer = ProtocolVersion.getProtocol(protocol.getProtocolVersion());
                line += " / C: " + clientVer + " S: " + serverVer + " A: " + protocol.getUser().isActive();
            }
        }

        info.getReturnValue().add(line);
    }
}
