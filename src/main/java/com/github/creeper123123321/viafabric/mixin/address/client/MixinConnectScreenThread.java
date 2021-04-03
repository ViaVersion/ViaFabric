package com.github.creeper123123321.viafabric.mixin.address.client;

import com.github.creeper123123321.viafabric.ViaFabricAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mixin(targets = "net/minecraft/client/gui/screen/ConnectScreen$1", priority = 2000)
public class MixinConnectScreenThread {
    @Redirect(method = "run()V", at = @At(value = "INVOKE",
            target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"))
    private InetAddress resolveViaFabricAddr(String address) throws UnknownHostException {
        ViaFabricAddress viaAddr = new ViaFabricAddress().parse(address);
        if (viaAddr.viaSuffix == null) {
            return InetAddress.getByName(address);
        }

        InetAddress resolved = InetAddress.getByName(viaAddr.realAddress);
        return InetAddress.getByAddress(resolved.getHostName() + "." + viaAddr.viaSuffix, resolved.getAddress());
    }
}