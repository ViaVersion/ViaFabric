package com.viaversion.fabric.mc118.platform;

import com.viaversion.fabric.mc118.providers.VRHandItemProvider;
import com.viaversion.fabric.mc118.providers.FabricVersionProvider;
import com.viaversion.fabric.mc118.providers.VRPlayerAbilitiesProvider;
import com.viaversion.fabric.mc118.providers.VRPlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class VFLoader implements ViaPlatformLoader {
    @Override
    public void load() {
        Via.getManager().getProviders().use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());
        Via.getManager().getProviders().use(VersionProvider.class, new FabricVersionProvider());

        if (Via.getPlatform().getConf().isItemCache()) {
            VRHandItemProvider handProvider = new VRHandItemProvider();
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                handProvider.registerClientTick();
            }
            Via.getManager().getProviders().use(HandItemProvider.class, handProvider);
        }
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Via.getManager().getProviders().use(PlayerAbilitiesProvider.class, new VRPlayerAbilitiesProvider());
            Via.getManager().getProviders().use(PlayerLookTargetProvider.class, new VRPlayerLookTargetProvider());
        }
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
