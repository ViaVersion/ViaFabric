package com.viaversion.fabric.mc119.mixin.signatures1_19_0;

import com.viaversion.fabric.mc119.signatures1_19_0.MessageSigner_1_19_0;
import com.viaversion.fabric.mc119.signatures1_19_0.ProtocolPatcher1_19_0;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ArgumentSignatureDataMap.class)
public class ArgumentSignatureDataMapMixin {

    @Inject(method = "sign", at = @At("HEAD"), cancellable = true)
    private static void injectSign(DecoratableArgumentList<?> arguments, ArgumentSignatureDataMap.ArgumentSigner signer, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (ProtocolPatcher1_19_0.shouldFixKeys) {
            final List<ArgumentSignatureDataMap.Entry> list = ArgumentSignatureDataMap.toNameValuePairs(arguments).stream().map(entry -> {
                final MessageMetadata metadata = MessageSigner_1_19_0.get();
                final MessageSignatureData messageSignatureData = MessageSigner_1_19_0.sign((Signer) signer, Text.literal(entry.getFirst()), metadata.sender(), metadata.timestamp(), metadata.salt());

                return new ArgumentSignatureDataMap.Entry(entry.getFirst(), messageSignatureData);
            }).collect(Collectors.toList());

            cir.setReturnValue(new ArgumentSignatureDataMap(list));
        }
    }
}
