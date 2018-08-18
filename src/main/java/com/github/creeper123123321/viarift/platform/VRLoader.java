package com.github.creeper123123321.viarift.platform;

import com.github.creeper123123321.viarift.provider.VRMovementTransmitter;
import com.github.creeper123123321.viarift.provider.VRVersionProvider;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.platform.ViaPlatformLoader;
import us.myles.ViaVersion.protocols.base.VersionProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class VRLoader implements ViaPlatformLoader {
    @Override
    public void load() {
        Via.getManager().getProviders().use(VersionProvider.class, new VRVersionProvider());
        Via.getManager().getProviders().use(MovementTransmitterProvider.class, new VRMovementTransmitter());
    }

    @Override
    public void unload() {

    }
}
