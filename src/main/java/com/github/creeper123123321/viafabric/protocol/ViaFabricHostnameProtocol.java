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
