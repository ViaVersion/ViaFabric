package com.viaversion.fabric.mc117.mixin.address.client;

import com.viaversion.fabric.mc117.ViaFabricAddress;
import com.google.common.net.HostAndPort;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerAddress.class)
public abstract class MixinServerAddress {
    @Shadow
    private static HostAndPort resolveServer(HostAndPort address) {
        throw new AssertionError();
    }

    @Redirect(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;resolveServer(Lcom/google/common/net/HostAndPort;)Lcom/google/common/net/HostAndPort;"))
    private static HostAndPort modifySrvAddr(HostAndPort address) {
        ViaFabricAddress viaAddr = new ViaFabricAddress().parse(address.getHost());
        if (viaAddr.viaSuffix == null) {
            return resolveServer(address);
        }

        HostAndPort resolved = resolveServer(HostAndPort.fromParts(viaAddr.realAddress, address.getPort()));
        return HostAndPort.fromParts(
                resolved.getHost().replaceAll("\\.$", "") + "." + viaAddr.viaSuffix, resolved.getPort());
    }
}
