/*
 * MIT License
 *
 * Copyright (c) 2018 creeper123123321 and contributors
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

package com.github.creeper123123321.viafabric.mixin.client;

import com.github.creeper123123321.viafabric.providers.VRVersionProvider;
import com.github.creeper123123321.viafabric.util.VersionFormatFilter;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.protocols.base.VersionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
    private TextFieldWidget protocolVersion;
    private boolean validProtocol = true;
    private boolean supportedProtocol;

    protected MixinMultiplayerScreen(TextComponent textComponent_1, UnsupportedOperationException e) {
        super(textComponent_1);
        throw e;
    }

    @Inject(method = "method_2540", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        protocolVersion = new TextFieldWidget(fontRenderer, this.screenWidth / 2 + 88, 13, 65, 15);
        protocolVersion.method_1890(new VersionFormatFilter());
        protocolVersion.setChangedListener((text) -> {
            protocolVersion.setSuggestion(null);
            int newVersion = ((VRVersionProvider) Via.getManager().getProviders().get(VersionProvider.class)).clientSideModeVersion;
            validProtocol = true;
            try {
                newVersion = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                ProtocolVersion closest = ProtocolVersion.getClosest(text);
                if (closest != null) {
                    newVersion = closest.getId();
                } else {
                    validProtocol = false;
                    List<String> completions = ProtocolVersion.getProtocols().stream()
                            .map(ProtocolVersion::getName)
                            .flatMap(str -> Stream.concat(
                                    Arrays.stream(str.split("-")),
                                    Arrays.stream(new String[]{str})
                            ))
                            .distinct()
                            .filter(ver -> ver.startsWith(text))
                            .collect(Collectors.toList());
                    if (completions.size() == 1) {
                        protocolVersion.setSuggestion(completions.get(0).substring(text.length()));
                    }
                }
            }
            supportedProtocol = isSupported(newVersion);
            protocolVersion.method_1868(getTextColor()); // Set editable color
            ((VRVersionProvider) Via.getManager().getProviders().get(VersionProvider.class)).clientSideModeVersion = newVersion;
        });
        int clientSideVersion = ((VRVersionProvider) Via.getManager().getProviders().get(VersionProvider.class)).clientSideModeVersion;
        protocolVersion.setText(ProtocolVersion.isRegistered(clientSideVersion)
                ? ProtocolVersion.getProtocol(clientSideVersion).getName()
                : Integer.toString(clientSideVersion));
        this.listeners.add(protocolVersion);
    }

    @Inject(method = "render",
            at = {
                @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Screen;render(IIF)V"),
                @At(value = "INVOKE", target = "Lnet/minecraft/class_437;render(IIF)V") // Generated refmap doesn't have it
            },
            remap = false)
    private void onRender(int int_1, int int_2, float float_1, CallbackInfo ci) {
        protocolVersion.render(int_1, int_2, float_1);
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(CallbackInfo ci) {
        protocolVersion.tick();
    }

    private int getTextColor() {
        if (!validProtocol) {
            return 0xff0000; // Red
        } else if (!supportedProtocol) {
            return 0xFFA500; // Orange
        }
        return 0xE0E0E0; // Default
    }

    private boolean isSupported(int protocol) {
        return ProtocolRegistry.getProtocolPath(ProtocolRegistry.SERVER_PROTOCOL, protocol) != null
                || ProtocolRegistry.SERVER_PROTOCOL == protocol;
    }
}
