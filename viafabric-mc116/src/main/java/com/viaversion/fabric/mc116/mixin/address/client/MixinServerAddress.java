package com.viaversion.fabric.mc116.mixin.address.client;

import com.mojang.datafixers.util.Pair;
import com.viaversion.fabric.common.AddressParser;
import net.minecraft.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerAddress.class)
public abstract class MixinServerAddress {
    @Shadow
    private static Pair<String, Integer> resolveServer(String address) {
        throw new AssertionError();
    }

    @Redirect(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ServerAddress;resolveServer(Ljava/lang/String;)Lcom/mojang/datafixers/util/Pair;"))
    private static Pair<String, Integer> modifySrvAddr(String address) {
        AddressParser viaAddr = new AddressParser().parse(address);
        if (viaAddr.viaSuffix == null) {
            return resolveServer(address);
        }

        return resolveServer(viaAddr.serverAddress).mapFirst(it -> it.replaceAll("\\.$", "") + "." + viaAddr.viaSuffix);
    }
}
