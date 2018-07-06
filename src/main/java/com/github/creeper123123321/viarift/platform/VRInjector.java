package com.github.creeper123123321.viarift.platform;

import com.github.creeper123123321.viarift.ViaRift;
import us.myles.ViaVersion.api.platform.ViaInjector;

public class VRInjector implements ViaInjector {
    @Override
    public void inject() throws Exception {
        // *looks at Mixins*
    }

    @Override
    public void uninject() throws Exception {
        // not possible *plays sad violin*
    }

    @Override
    public int getServerProtocolVersion() throws Exception {
        return ViaRift.fakeServerVersion;
    }

    @Override
    public String getEncoderName() {
        return "decoder";
    }

    @Override
    public String getDecoderName() {
        return "encoder";
    }
}
