package com.viaversion.fabric.mc115.commands;

import com.viaversion.fabric.mc115.ViaFabric;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class VRClientCommands implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        commandDispatcher.register(ViaFabric.command("viafabricclient"));
    }
}
