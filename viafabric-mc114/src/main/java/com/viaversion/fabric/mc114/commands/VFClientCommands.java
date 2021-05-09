package com.viaversion.fabric.mc114.commands;

import com.viaversion.fabric.mc114.ViaFabric;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class VFClientCommands implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        commandDispatcher.register(ViaFabric.command("viafabricclient"));
    }
}
