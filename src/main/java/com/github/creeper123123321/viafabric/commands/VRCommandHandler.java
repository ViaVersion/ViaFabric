/*
 * MIT License
 *
 * Copyright (c) 2018- creeper123123321 <https://creeper123123321.keybase.pub/>
 * Copyright (c) 2019- contributors <https://github.com/ViaVersion/ViaFabric/graphs/contributors>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.creeper123123321.viafabric.commands;

import com.github.creeper123123321.viafabric.commands.subs.LeakDetectSubCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import us.myles.ViaVersion.commands.ViaCommandHandler;

import java.util.concurrent.CompletableFuture;

public class VRCommandHandler extends ViaCommandHandler {
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
