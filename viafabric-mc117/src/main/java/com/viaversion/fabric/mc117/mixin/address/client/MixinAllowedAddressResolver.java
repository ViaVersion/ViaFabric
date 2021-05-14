package com.viaversion.fabric.mc117.mixin.address.client;

import com.viaversion.fabric.common.VFAddressParser;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.RedirectResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Final;
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
    @Final
    private RedirectResolver redirectResolver;

    @Shadow
    protected abstract Optional<Address> getAllowedAddress(ServerAddress address);

    @Inject(method = "resolve", at = @At(value = "HEAD"), cancellable = true)
    private void resolveVF(ServerAddress address, CallbackInfoReturnable<Optional<Address>> cir) throws UnknownHostException {
        VFAddressParser viaAddr = new VFAddressParser().parse(address.getAddress());
        if (viaAddr.viaSuffix == null) {
            return;
        }

        ServerAddress realAddress = new ServerAddress(viaAddr.realAddress, address.getPort());

        cir.setReturnValue(getAllowedAddress(realAddress)
                .map(it -> redirectResolver.lookupRedirect(realAddress)
                        .flatMap(this::getAllowedAddress)
                        .orElse(it))
                .map(it -> {
                    try {
                        return Address.create(new InetSocketAddress(InetAddress.getByAddress(
                                it.getHostName() + "." + viaAddr.viaSuffix,
                                it.getInetSocketAddress().getAddress().getAddress()), it.getPort()
                        ));
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }
}
