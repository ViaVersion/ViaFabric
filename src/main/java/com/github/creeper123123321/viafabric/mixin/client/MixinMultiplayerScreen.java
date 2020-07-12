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

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.gui.ViaButton;
import com.github.creeper123123321.viafabric.util.VersionFormatFilter;
import net.minecraft.class_703;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
    @Unique
    private TextFieldWidget protocolVersion;
    @Unique
    private ButtonWidget enableClientSideViaVersion;
    @Unique
    private boolean validProtocol;
    @Unique
    private boolean supportedProtocol;
    @Unique
    private CompletableFuture<Void> latestProtocolSave;

    protected MixinMultiplayerScreen(UnsupportedOperationException e) {
        super();
        throw e;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        protocolVersion = new TextFieldWidget("viafabric client version".hashCode(),
                textRenderer, this.width / 2 + 88, 13, 65, 15);
        protocolVersion.setTextPredicate(new VersionFormatFilter());
        protocolVersion.setListener(new class_703.WidgetListener() {
            @Override
            public void method_2596(int i, boolean bl) {
            }

            @Override
            public void method_2594(int i, float f) {
            }

            @Override
            public void textModified(int id, String text) {
                MixinMultiplayerScreen.this.textModified(text);
            }
        });
        int clientSideVersion = ViaFabric.config.getClientSideVersion();

        protocolVersion.setVisible(ViaFabric.config.isClientSideEnabled());

        protocolVersion.setText(ProtocolVersion.isRegistered(clientSideVersion)
                ? ProtocolVersion.getProtocol(clientSideVersion).getName()
                : Integer.toString(clientSideVersion));
        textModified(protocolVersion.getText());
        //this.children.add(protocolVersion);

        enableClientSideViaVersion = new ViaButton("via button".hashCode(),this.width / 2 + 113, 10,
                40, 20, // Size
                0, 0, // Start pos of texture
                20, // v Hover offset
                new Identifier("viafabric:textures/gui/via_button.png"),
                256, 256, // Texture size (1.8 is 256x256)
                button -> MinecraftClient.getInstance().openScreen(new ConfirmScreen(
                        (answer, id) -> {
                            MinecraftClient.getInstance().openScreen(this);
                            if (answer) {
                                ViaFabric.config.setClientSideEnabled(true);
                                ViaFabric.config.saveConfig();
                                protocolVersion.setVisible(true);
                                enableClientSideViaVersion.visible = false;
                            }
                        },
                        I18n.translate("gui.enable_client_side.question"),
                        I18n.translate("gui.enable_client_side.warning"),
                        I18n.translate("gui.enable_client_side.enable"),
                        I18n.translate("gui.cancel"),
                        "via confirm".hashCode()
                )),
                I18n.translate("gui.enable_client_side_button")
                );
        enableClientSideViaVersion.visible = !protocolVersion.isVisible();
        this.buttons.add(enableClientSideViaVersion);
    }

    private void textModified(String text) {
        //protocolVersion.setSuggestion(null);
        int newVersion = ViaFabric.config.getClientSideVersion();
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
                //if (completions.size() == 1) {
                //    protocolVersion.setSuggestion(completions.get(0).substring(text.length()));
                //}
            }
        }
        supportedProtocol = isSupported(newVersion);
        protocolVersion.setEditableColor(getTextColor());
        int finalNewVersion = newVersion;
        if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
        latestProtocolSave = latestProtocolSave.thenRunAsync(() -> {
            ViaFabric.config.setClientSideVersion(finalNewVersion);
            ViaFabric.config.saveConfig();
        }, ViaFabric.ASYNC_EXECUTOR);
    }

    @Inject(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V"),
    })
    private void onRender(int int_1, int int_2, float float_1, CallbackInfo ci) {
        protocolVersion.render();
    }

    @Inject(method = "keyPressed", at = {@At("TAIL")})
    private void onKey(char character, int code, CallbackInfo ci) {
        protocolVersion.keyPressed(character, code);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        protocolVersion.tick();
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void onMouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
        protocolVersion.mouseClicked(mouseX, mouseY, button);
    }

    @Unique
    private int getTextColor() {
        if (!validProtocol) {
            return 0xff0000; // Red
        } else if (!supportedProtocol) {
            return 0xFFA500; // Orange
        }
        return 0xE0E0E0; // Default
    }

    @Unique
    private boolean isSupported(int protocol) {
        return ProtocolRegistry.getProtocolPath(ProtocolRegistry.SERVER_PROTOCOL, protocol) != null
                || ProtocolRegistry.SERVER_PROTOCOL == protocol;
    }

}
