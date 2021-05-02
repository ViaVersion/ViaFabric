package com.viaversion.fabric.mc18.commands;

import com.viaversion.fabric.mc18.commands.subs.LeakDetectSubCommand;
import com.viaversion.viaversion.commands.ViaCommandHandler;

public class VRCommandHandler extends ViaCommandHandler {
    {
        try {
            registerSubCommand(new LeakDetectSubCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
