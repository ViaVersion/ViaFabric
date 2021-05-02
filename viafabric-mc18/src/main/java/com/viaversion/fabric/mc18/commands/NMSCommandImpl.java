package com.viaversion.fabric.mc18.commands;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import com.viaversion.viaversion.api.command.ViaVersionCommand;

import java.util.Arrays;
import java.util.List;

public class NMSCommandImpl extends AbstractCommand {
    private ViaVersionCommand handler;

    public NMSCommandImpl(ViaVersionCommand handler) {
        this.handler = handler;
    }

    @Override
    public String getCommandName() {
        return "viaversion";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("vvfabric", "viaver");
    }

    @Override
    public String getUsageTranslationKey(CommandSource commandSource) {
        return "/viaversion [help|subcommand]";
    }

    @Override
    public void execute(CommandSource commandSource, String[] strings) {
        handler.onCommand(new NMSCommandSender(commandSource), strings);
    }

    @Override
    public List<String> getAutoCompleteHints(CommandSource commandSource, String[] strings, BlockPos blockPos) {
        return handler.onTabComplete(new NMSCommandSender(commandSource), strings);
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
