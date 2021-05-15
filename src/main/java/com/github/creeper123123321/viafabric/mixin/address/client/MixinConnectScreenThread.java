package com.github.creeper123123321.viafabric.mixin.address.client;

import com.github.creeper123123321.viafabric.ViaFabricAddress;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Mixin(targets = "net/minecraft/client/gui/screen/ConnectScreen$1", priority = 2000)
public class MixinConnectScreenThread {
    @Redirect(method = "run()V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/AllowedAddressResolver;resolve(Lnet/minecraft/client/network/ServerAddress;)Ljava/util/Optional;"))
    private Optional<Address> resolveViaFabricAddr(AllowedAddressResolver allowedAddressResolver, ServerAddress address) throws UnknownHostException {
        ViaFabricAddress viaAddr = new ViaFabricAddress().parse(address.getAddress());
        if (viaAddr.viaSuffix == null) {
            return allowedAddressResolver.resolve(address);
        }

        Address resolved = allowedAddressResolver.resolve(ServerAddress.parse(viaAddr.realAddress)).orElse(null);
        if (resolved == null) return Optional.empty();

        InetAddress resolvedAddr = InetAddress.getByAddress(
                resolved.getHostName() + "." + viaAddr.viaSuffix,
                resolved.getInetSocketAddress().getAddress().getAddress()
        );
        return Optional.of(Address.create(new InetSocketAddress(resolvedAddr, address.getPort())));
    }
}