package com.github.creeper123123321.viarift.mixin.client;

import com.github.creeper123123321.viarift.ViaRift;
import com.github.creeper123123321.viarift.gui.multiplayer.SaveProtocolButton;
import com.github.creeper123123321.viarift.util.IntegerFormatFilter;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen {
    private GuiTextField protocolVersion;
    private GuiButton saveProtocol;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void onInitGui(CallbackInfo ci) {
        protocolVersion = new GuiTextField(1235, fontRenderer, this.width / 2 + 70, 8, 30, 20);
        protocolVersion.setText(Integer.toString(ViaRift.fakeServerVersion));
        protocolVersion.func_200675_a(new IntegerFormatFilter());
        this.field_195124_j.add(protocolVersion);
        saveProtocol = new SaveProtocolButton(6356, width / 2 + 100, 8, 50, 20,
                I18n.format("gui.save_protocol_version"), protocolVersion);
        this.field_195124_j.add(saveProtocol);
        addButton(saveProtocol);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void onDrawScreen(int p_1, int p_2, float p_3, CallbackInfo ci) {
        drawCenteredString(fontRenderer, I18n.format("gui.protocol_version"),this.width / 2, 12, 0xFFFFFF);
        protocolVersion.func_195608_a(p_1, p_2, p_3);
    }

    @Inject(method = "updateScreen", at = @At("TAIL"))
    private void onUpdateScreen(CallbackInfo ci) {
        protocolVersion.updateCursorCounter();
    }

    @Inject(method = "getFocused", at = @At("RETURN"), cancellable = true)
    private void onGetFocused(CallbackInfoReturnable<IGuiEventListener> cir){
        if (protocolVersion.isFocused()) {
            cir.setReturnValue(protocolVersion);
        }
    }
}
