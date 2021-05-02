package com.viaversion.fabric.mc18.gui;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

public class ListeneableButton extends ButtonWidget {
    private Consumer<ButtonWidget> click;

    public ListeneableButton(int id, int x, int y, String message, Consumer<ButtonWidget> click) {
        super(id, x, y, message);
        this.click = click;
    }

    public ListeneableButton(int id, int x, int y, int width, int height, String message, Consumer<ButtonWidget> click) {
        super(id, x, y, width, height, message);
        this.click = click;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
        click.accept(this);
    }
}
