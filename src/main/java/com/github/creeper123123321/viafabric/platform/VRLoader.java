package com.github.creeper123123321.viafabric.platform;

import com.github.creeper123123321.viafabric.providers.VRHandItemProvider;
import com.github.creeper123123321.viafabric.providers.VRVersionProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.platform.ViaPlatformLoader;
import us.myles.ViaVersion.bungee.providers.BungeeMovementTransmitter;
import us.myles.ViaVersion.protocols.base.VersionProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class VRLoader implements ViaPlatformLoader {
    @Override
    public void load() {
        Via.getManager().getProviders().use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());
        Via.getManager().getProviders().use(VersionProvider.class, new VRVersionProvider());

        if (Via.getPlatform().getConf().isItemCache()) {
            VRHandItemProvider handProvider = new VRHandItemProvider();
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                handProvider.registerClientTick();
            }
            handProvider.registerServerTick();
            Via.getManager().getProviders().use(HandItemProvider.class, handProvider);
        }
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
