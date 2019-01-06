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

import com.github.creeper123123321.viafabric.gui.multiplayer.SaveProtocolButton;
import com.github.creeper123123321.viafabric.util.VersionFormatFilter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.menu.MultiplayerGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

@Mixin(MultiplayerGui.class)
public abstract class MixinMultiplayerGui extends Gui {
    private TextFieldWidget protocolVersion;

    @Inject(method = "onInitialized", at = @At("TAIL"))
    private void onOnInitialized(CallbackInfo ci) {
        protocolVersion = new TextFieldWidget(1235, fontRenderer, this.width / 2 + 55, 8, 45, 20);
        protocolVersion.setText(ProtocolVersion.isRegistered(ProtocolRegistry.SERVER_PROTOCOL)
                ? ProtocolVersion.getProtocol(ProtocolRegistry.SERVER_PROTOCOL).getName()
                : Integer.toString(ProtocolRegistry.SERVER_PROTOCOL));
        protocolVersion.method_1890(new VersionFormatFilter());
        this.listeners.add(protocolVersion);
        addButton(new SaveProtocolButton(6356, width / 2 + 100, 8, 50, 20,
                I18n.translate("selectWorld.edit.save"), protocolVersion));
    }

    @Inject(method = "draw", at = @At("TAIL"))
    private void onDraw(int p_1, int p_2, float p_3, CallbackInfo ci) {
        drawStringCentered(fontRenderer, "Protocol Version:", this.width / 2, 12, 0xFFFFFF);
        protocolVersion.render(p_1, p_2, p_3);
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(CallbackInfo ci) {
        protocolVersion.tick();
    }

    @Inject(method = "getFocused", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGetFocused(CallbackInfoReturnable<GuiEventListener> cir) {
        if (protocolVersion.isFocused()) {
            cir.setReturnValue(protocolVersion);
        }
    }
}
