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
import com.github.creeper123123321.viafabric.gui.ProtocolVersionFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.concurrent.CompletableFuture;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
    @Unique
    private ProtocolVersionFieldWidget protocolVersion;
    @Unique
    private ButtonWidget enableClientSideViaVersion;
    @Unique
    private CompletableFuture<Void> latestProtocolSave;

    protected MixinMultiplayerScreen(Text title, UnsupportedOperationException e) {
        super(title);
        throw e;
    }

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        protocolVersion = new ProtocolVersionFieldWidget(font, this.width / 2 + 88, 13, 65, 15);
        protocolVersion.setProtocolChangedListener(protocol -> {
            if (protocol == null) return;

            if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
            latestProtocolSave = latestProtocolSave.thenRunAsync(() -> {
                ViaFabric.config.setClientSideVersion(protocol);
                ViaFabric.config.saveConfig();
            }, ViaFabric.ASYNC_EXECUTOR);
        });
        protocolVersion.setVisible(ViaFabric.config.isClientSideEnabled());
        int clientSideVersion = ViaFabric.config.getClientSideVersion();
        protocolVersion.setText(ProtocolVersion.isRegistered(clientSideVersion)
                ? ProtocolVersion.getProtocol(clientSideVersion).getName()
                : Integer.toString(clientSideVersion));
        this.children.add(protocolVersion);

        enableClientSideViaVersion = new TexturedButtonWidget(this.width / 2 + 113, 10,
                40, 20, // Size
                0, 0, // Start pos of texture
                20, // v Hover offset
                new Identifier("viafabric:textures/gui/via_button.png"),
                64, 64, // Texture size
                button -> MinecraftClient.getInstance().openScreen(new ConfirmScreen(
                        answer -> {
                            MinecraftClient.getInstance().openScreen(this);
                            if (answer) {
                                ViaFabric.config.setClientSideEnabled(true);
                                ViaFabric.config.saveConfig();
                                protocolVersion.setVisible(true);
                                enableClientSideViaVersion.visible = false;
                            }
                        },
                        new TranslatableText("gui.enable_client_side.question"),
                        new TranslatableText("gui.enable_client_side.warning"),
                        I18n.translate("gui.enable_client_side.enable"),
                        I18n.translate("gui.cancel")
                )),
                I18n.translate("gui.enable_client_side_button"));
        enableClientSideViaVersion.visible = !protocolVersion.isVisible();
        addButton(enableClientSideViaVersion);
    }

    @Inject(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Screen;render(IIF)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/class_437;render(IIF)V") // todo check if refmap was fixed
    }, remap = false)
    private void onRender(int int_1, int int_2, float float_1, CallbackInfo ci) {
        protocolVersion.render(int_1, int_2, float_1);
    }

    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    private void onTick(CallbackInfo ci) {
        protocolVersion.tick();
    }
}
