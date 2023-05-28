package com.viaversion.fabric.mc116.commands;

import com.viaversion.fabric.common.commands.subs.LeakDetectSubCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import com.viaversion.viaversion.commands.ViaCommandHandler;

import java.util.concurrent.CompletableFuture;

public class VFCommandHandler extends ViaCommandHandler {
    {
        try {
            registerSubCommand(new LeakDetectSubCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int execute(CommandContext<? extends CommandSource> ctx) {
        String[] args = new String[0];
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ");
        } catch (IllegalArgumentException ignored) {
        }
        onCommand(
                new NMSCommandSender(ctx.getSource()),
                args
        );
        return 1;
    }

    public CompletableFuture<Suggestions> suggestion(CommandContext<? extends CommandSource> ctx, SuggestionsBuilder builder) {
        String[] args;
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ", -1);
        } catch (IllegalArgumentException ignored) {
            args = new String[]{""};
        }
        String[] pref = args.clone();
        pref[pref.length - 1] = "";
        String prefix = String.join(" ", pref);
        onTabComplete(new NMSCommandSender(ctx.getSource()), args)
                .stream()
                .map(it -> {
                    SuggestionsBuilder b = new SuggestionsBuilder(builder.getInput(), prefix.length() + builder.getStart());
                    b.suggest(it);
                    return b;
                })
                .forEach(builder::add);
        return builder.buildFuture();
    }
}
