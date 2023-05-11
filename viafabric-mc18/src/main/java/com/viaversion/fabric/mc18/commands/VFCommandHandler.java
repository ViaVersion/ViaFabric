package com.viaversion.fabric.mc18.commands;

import com.viaversion.fabric.common.commands.subs.LeakDetectSubCommand;
import com.viaversion.viaversion.commands.ViaCommandHandler;

public class VFCommandHandler extends ViaCommandHandler {
    {
        try {
            registerSubCommand(new LeakDetectSubCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
