package com.github.creeper123123321.viarift.provider;

import com.github.creeper123123321.viarift.ViaRift;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.base.VersionProvider;

public class VRVersionProvider extends VersionProvider {
    @Override
    public int getServerProtocol(UserConnection connection) throws Exception {
        return ViaRift.fakeServerVersion;
    }
}
