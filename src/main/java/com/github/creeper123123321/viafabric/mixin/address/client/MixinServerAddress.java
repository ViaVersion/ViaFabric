/*
 * MIT License
 *
 * Copyright (c) 2018- creeper123123321 <https://creeper123123321.keybase.pub/>
 * Copyright (c) 2019- contributors <https://github.com/ViaVersion/ViaFabric/graphs/contributors>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.mixin.address.client;

import com.github.creeper123123321.viafabric.ViaFabricAddress;
import net.minecraft.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerAddress.class)
public abstract class MixinServerAddress {
    @Shadow private static String[] resolveSrv(String address) {
        throw new AssertionError();
    }

    @Redirect(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ServerAddress;resolveSrv(Ljava/lang/String;)[Ljava/lang/String;"))
    private static String[] modifySrvAddr(String address) {
        ViaFabricAddress viaAddr = new ViaFabricAddress().parse(address);
        if (viaAddr.viaSuffix == null) {
            return resolveSrv(address);
        }

        String[] resolvedSrv = resolveSrv(viaAddr.realAddress);
        resolvedSrv[0] = resolvedSrv[0].replaceAll("\\.$", "") + "." + viaAddr.viaSuffix;

        return resolvedSrv;
    }
}
