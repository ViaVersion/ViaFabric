package com.viaversion.fabric.mc119.mixin.signatures1_19.client;

import com.viaversion.fabric.mc119.signatures1_19.IPublicKeyData;
import com.viaversion.fabric.mc119.signatures1_19.ProtocolPatcher1_19;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

@Mixin(PlayerPublicKey.PublicKeyData.class)
public class PlayerPublicKeyChPublicKeyDataMixin implements IPublicKeyData {

    @Shadow
    @Final
    private Instant expiresAt;

    @Shadow
    @Final
    PublicKey key;

    @Unique
    private byte[] key1_19_0;

    @Redirect(method = {"write", "verifyKey"}, at = @At(value = "FIELD", target = "Lnet/minecraft/network/encryption/PlayerPublicKey$PublicKeyData;keySignature:[B"))
    public byte[] replaceKeys(PlayerPublicKey.PublicKeyData instance) {
        if (this.key1_19_0 != null && ProtocolPatcher1_19.shouldPatchKeys) {
            return this.key1_19_0;
        }

        return instance.keySignature();
    }

    @Inject(method = "toSerializedString", at = @At(value = "HEAD"), cancellable = true)
    public void injectToSerializedString(UUID playerUuid, CallbackInfoReturnable<byte[]> cir) {
        if (ProtocolPatcher1_19.shouldPatchKeys) {
            cir.setReturnValue((this.expiresAt.toEpochMilli() + NetworkEncryptionUtils.encodeRsaPublicKey(this.key)).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void set1_19_0Key(ByteBuffer byteBuffer) {
        this.key1_19_0 = byteBuffer.array();
    }
}
