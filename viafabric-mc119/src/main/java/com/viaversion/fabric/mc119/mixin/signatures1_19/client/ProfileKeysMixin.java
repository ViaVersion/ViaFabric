package com.viaversion.fabric.mc119.mixin.signatures1_19.client;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.viaversion.fabric.mc119.signatures1_19.IPublicKeyData;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProfileKeys.class)
public class ProfileKeysMixin {
    @Inject(method = "decodeKeyPairResponse", at = @At("RETURN"))
    private static void trackLegacyKey(KeyPairResponse keyPairResponse, CallbackInfoReturnable<PlayerPublicKey.PublicKeyData> cir) {
        ((IPublicKeyData) (Object) cir.getReturnValue()).set1_19_0Key(keyPairResponse.getLegacyPublicKeySignature());
    }
}
