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

package com.github.creeper123123321.viafabric.protocol;

import com.github.creeper123123321.viafabric.ViaFabricAddress;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.SimpleProtocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueTransformer;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;

public class ViaFabricHostnameProtocol extends SimpleProtocol {
    public static final ViaFabricHostnameProtocol INSTANCE = new ViaFabricHostnameProtocol();

    @Override
    protected void registerPackets() {
        registerIncoming(State.HANDSHAKE, 0, 0, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Protocol version
                map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                    @Override
                    public String transform(PacketWrapper packetWrapper, String s) {
                        return new ViaFabricAddress().parse(s).realAddress;
                    }
                });
            }
        });
    }
}
