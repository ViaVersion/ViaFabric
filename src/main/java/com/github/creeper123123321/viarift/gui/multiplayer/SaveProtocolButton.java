package com.github.creeper123123321.viarift.gui.multiplayer;

import com.github.creeper123123321.viarift.ViaRift;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class SaveProtocolButton extends GuiButton {
    private GuiTextField textField;

    public SaveProtocolButton(int id, int x, int y, int width, int height, String text, GuiTextField tf) {
        super(id, x, y, width, height, text);
        textField = tf;
    }

    @Override
    public void mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_) {
        try {
            ViaRift.fakeServerVersion = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            textField.setText(Integer.toString(ViaRift.fakeServerVersion));
        }
    }
}
