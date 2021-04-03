package com.github.creeper123123321.viafabric.commands.subs;

import io.netty.util.ResourceLeakDetector;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LeakDetectSubCommand extends ViaSubCommand {
    @Override
    public String name() {
        return "leakdetect";
    }

    @Override
    public String description() {
        return "Sets ResourceLeakDetector level";
    }

    @Override
    public boolean execute(ViaCommandSender viaCommandSender, String[] strings) {
        if (strings.length == 1) {
            try {
                ResourceLeakDetector.Level level = ResourceLeakDetector.Level.valueOf(strings[0]);
                ResourceLeakDetector.setLevel(level);
                viaCommandSender.sendMessage("Set leak detector level to " + level);
            } catch (IllegalArgumentException e) {
                viaCommandSender.sendMessage("Invalid level (" + Arrays.toString(ResourceLeakDetector.Level.values()) + ")");
            }
        } else {
            viaCommandSender.sendMessage("Current leak detection level is " + ResourceLeakDetector.getLevel());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(ResourceLeakDetector.Level.values())
                    .map(Enum::name)
                    .filter(it -> it.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return super.onTabComplete(sender, args);
    }
}
