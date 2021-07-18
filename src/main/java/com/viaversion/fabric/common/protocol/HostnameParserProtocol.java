package com.viaversion.fabric.common.protocol;

import com.viaversion.fabric.common.AddressParser;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.packet.State;

public class HostnameParserProtocol extends AbstractSimpleProtocol {
    public static final HostnameParserProtocol INSTANCE = new HostnameParserProtocol();

    @Override
    protected void registerPackets() {
        registerServerbound(State.HANDSHAKE, 0, 0, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Protocol version
                map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                    @Override
                    public String transform(PacketWrapper packetWrapper, String s) {
                        return new AddressParser().parse(s).serverAddress;
                    }
                });
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
