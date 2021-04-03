package com.github.creeper123123321.viafabric.commands;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class VRClientCommands implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        commandDispatcher.register(ViaFabric.command("viafabricclient"));
    }
}
