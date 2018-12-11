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

package com.github.creeper123123321.viarift.mixin.client;

import com.github.creeper123123321.viarift.gui.multiplayer.SaveProtocolButton;
import com.github.creeper123123321.viarift.util.IntegerFormatFilter;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen {
    private GuiTextField protocolVersion;
    private GuiButton saveProtocol;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void onInitGui(CallbackInfo ci) {
        protocolVersion = new GuiTextField(1235, fontRenderer, this.width / 2 + 70, 8, 30, 20);
        protocolVersion.setText(Integer.toString(ProtocolRegistry.SERVER_PROTOCOL));
        protocolVersion.setValidator(new IntegerFormatFilter());
        this.children.add(protocolVersion);
        saveProtocol = new SaveProtocolButton(6356, width / 2 + 100, 8, 50, 20,
                I18n.format("gui.save_protocol_version"), protocolVersion);
        addButton(saveProtocol);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onDrawScreen(int p_1, int p_2, float p_3, CallbackInfo ci) {
        drawCenteredString(fontRenderer, I18n.format("gui.protocol_version"),this.width / 2, 12, 0xFFFFFF);
        protocolVersion.drawTextField(p_1, p_2, p_3);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onUpdateScreen(CallbackInfo ci) {
        protocolVersion.tick();
    }

    @Inject(method = "getFocused", at = @At("RETURN"), cancellable = true)
    private void onGetFocused(CallbackInfoReturnable<IGuiEventListener> cir){
        if (protocolVersion.isFocused()) {
            cir.setReturnValue(protocolVersion);
        }
    }
}
