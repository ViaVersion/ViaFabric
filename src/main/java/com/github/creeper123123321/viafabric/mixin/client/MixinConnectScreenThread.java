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

package com.github.creeper123123321.viafabric.mixin.client;

import com.github.creeper123123321.viafabric.ViaFabricAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mixin(targets = "net/minecraft/client/gui/screen/ConnectScreen$1", priority = 2000) // don't know if it will work with MinerParty mod
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