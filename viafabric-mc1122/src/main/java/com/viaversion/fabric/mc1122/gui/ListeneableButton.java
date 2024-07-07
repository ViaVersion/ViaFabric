/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.fabric.mc1122.gui;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.Consumer;

public class ListeneableButton extends ButtonWidget {
    private final Consumer<ButtonWidget> click;

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
