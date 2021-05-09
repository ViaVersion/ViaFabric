package com.viaversion.fabric.mc114.mixin.address.client;

import com.viaversion.fabric.common.VFAddressParser;
import net.minecraft.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerAddress.class)
public abstract class MixinServerAddress {
    @Shadow
    private static String[] resolveSrv(String address) {
        throw new AssertionError();
    }

    @Redirect(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ServerAddress;resolveSrv(Ljava/lang/String;)[Ljava/lang/String;"))
    private static String[] modifySrvAddr(String address) {
        VFAddressParser viaAddr = new VFAddressParser().parse(address);
        if (viaAddr.viaSuffix == null) {
            return resolveSrv(address);
        }

        String[] resolvedSrv = resolveSrv(viaAddr.realAddress);
        resolvedSrv[0] = resolvedSrv[0].replaceAll("\\.$", "") + "." + viaAddr.viaSuffix;

        return resolvedSrv;
    }
}
