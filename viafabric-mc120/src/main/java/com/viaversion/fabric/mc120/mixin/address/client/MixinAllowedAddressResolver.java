package com.viaversion.fabric.mc120.mixin.address.client;

import com.viaversion.fabric.common.AddressParser;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Mixin(AllowedAddressResolver.class)
public abstract class MixinAllowedAddressResolver {
    @Shadow
    public abstract Optional<Address> resolve(ServerAddress address);

    @Inject(method = "resolve", at = @At(value = "HEAD"), cancellable = true)
    private void resolveVF(ServerAddress address, CallbackInfoReturnable<Optional<Address>> cir) {
        AddressParser viaAddr = new AddressParser().parse(address.getAddress());
        if (viaAddr.viaSuffix == null) {
            return;
        }

        ServerAddress realAddress = new ServerAddress(viaAddr.serverAddress, address.getPort());

        cir.setReturnValue(resolve(realAddress).map(it -> viaFabricAddSuffix(it, viaAddr.getSuffixWithOptions())));
    }

    private Address viaFabricAddSuffix(Address it, String viaSuffix) {
        try {
            return Address.create(new InetSocketAddress(
                    InetAddress.getByAddress(it.getHostName() + "." + viaSuffix,
                            it.getInetSocketAddress().getAddress().getAddress()), it.getPort()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
