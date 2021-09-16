package com.viaversion.fabric.mc18.mixin.address.client;

import com.viaversion.fabric.common.AddressParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreenThread {
    @Redirect(method = "run()V", at = @At(value = "INVOKE",
            target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"))
    private InetAddress resolveViaFabricAddr(String address) throws UnknownHostException {
        AddressParser viaAddr = new AddressParser().parse(address);
        if (viaAddr.viaSuffix == null) {
            return InetAddress.getByName(address);
        }

        InetAddress resolved = InetAddress.getByName(viaAddr.serverAddress);
        return InetAddress.getByAddress(resolved.getHostName() + "." + viaAddr.getSuffixWithOptions(), resolved.getAddress());
    }
}