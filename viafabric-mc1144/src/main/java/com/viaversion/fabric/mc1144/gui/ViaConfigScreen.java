/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2026 ViaVersion and contributors
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
package com.viaversion.fabric.mc1144.gui;

import com.viaversion.fabric.common.config.AbstractViaConfigScreen;
import com.viaversion.fabric.common.util.ProtocolUtils;
import com.viaversion.fabric.mc1144.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(EnvType.CLIENT)
public class ViaConfigScreen extends Screen implements AbstractViaConfigScreen {
    private static CompletableFuture<Void> latestProtocolSave;
    private final Screen parent;
    private EditBox protocolVersion;

    public ViaConfigScreen(Screen parent) {
        super(new TranslatableComponent(TITLE_TRANSLATE_ID));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int entries = 0;

        this.addButton(new Button(calculatePosX(this.width, entries),
            calculatePosY(this.height, entries),
            150,
            20, getClientSideText().getContents(), this::onClickClientSide));
        entries++;

        this.addButton(new Button(calculatePosX(this.width, entries),
            calculatePosY(this.height, entries),
            150,
            20, getHideViaButtonText().getContents(), this::onHideViaButton));
        entries++;

        protocolVersion = new EditBox(this.font,
            calculatePosX(this.width, entries),
            calculatePosY(this.height, entries),
            150,
            20, new TranslatableComponent(VERSION_TRANSLATE_ID).getContents());
        entries++;

        protocolVersion.setFilter(ProtocolUtils::isStartOfProtocolText);
        protocolVersion.setResponder(this::onChangeVersionField);
        int clientSideVersion = ViaFabric.config.getClientSideVersion();
        protocolVersion.setValue(ProtocolUtils.getProtocolName(clientSideVersion));

        this.children.add(protocolVersion);

        this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableComponent("gui.done").getContents(), (buttonWidget) -> Minecraft.getInstance().setScreen(this.parent)));
    }

    private void onChangeVersionField(String text) {
        protocolVersion.setSuggestion(null);
        int newVersion = ViaFabric.config.getClientSideVersion();

        Integer parsed = ProtocolUtils.parseProtocolId(text);
        boolean validProtocol;

        if (parsed != null) {
            newVersion = parsed;
            validProtocol = true;
        } else {
            validProtocol = false;
            String[] suggestions = ProtocolUtils.getProtocolSuggestions(text);
            if (suggestions.length == 1) {
                protocolVersion.setSuggestion(suggestions[0].substring(text.length()));
            }
        }

        protocolVersion.setTextColor(getProtocolTextColor(ProtocolVersion.getProtocol(newVersion), validProtocol));

        int finalNewVersion = newVersion;
        if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
        ViaFabric.config.setClientSideVersion(finalNewVersion);
        latestProtocolSave = latestProtocolSave.thenRunAsync(ViaFabric.config::save, ViaFabric.ASYNC_EXECUTOR);
    }

    private void onClickClientSide(Button widget) {
        if (!ViaFabric.config.isClientSideEnabled()) {
            Minecraft.getInstance().setScreen(new ConfirmScreen(
                answer -> {
                    if (answer) {
                        ViaFabric.config.setClientSideEnabled(true);
                        ViaFabric.config.save();
                        widget.setMessage(getClientSideText().getContents());
                    }
                    Minecraft.getInstance().setScreen(this);
                },
                new TranslatableComponent("gui.enable_client_side.question"),
                new TranslatableComponent("gui.enable_client_side.warning"),
                new TranslatableComponent("gui.enable_client_side.enable").getContents(),
                new TranslatableComponent("gui.cancel").getContents()
            ));
        } else {
            ViaFabric.config.setClientSideEnabled(false);
            ViaFabric.config.save();
        }
        widget.setMessage(getClientSideText().getContents());
    }

    @Override
    public void removed() {
        ViaFabric.config.save();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    private TranslatableComponent getClientSideText() {
        return ViaFabric.config.isClientSideEnabled() ?
            new TranslatableComponent("gui.client_side.disable")
            : new TranslatableComponent("gui.client_side.enable");
    }

    private TranslatableComponent getHideViaButtonText() {
        return ViaFabric.config.isHideButton() ?
            new TranslatableComponent("gui.hide_via_button.disable") : new TranslatableComponent("gui.hide_via_button.enable");
    }

    private void onHideViaButton(Button widget) {
        ViaFabric.config.setHideButton(!ViaFabric.config.isHideButton());
        ViaFabric.config.save();
        widget.setMessage(getHideViaButtonText().getContents());
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        drawCenteredString(this.font, this.title.getContents(), this.width / 2, 20, 0xFFFFFFFF);
        super.render(mouseX, mouseY, delta);
        protocolVersion.render(mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        protocolVersion.tick();
    }
}

