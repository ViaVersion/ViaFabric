/*
 * This file is part of ViaFabric - https://github.com/ViaVersion/ViaFabric
 * Copyright (C) 2018-2025 ViaVersion and contributors
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
package com.viaversion.fabric.mc1194.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.viaversion.fabric.common.config.AbstractViaConfigScreen;
import com.viaversion.fabric.common.util.ProtocolUtils;
import com.viaversion.fabric.mc1194.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ViaConfigScreen extends Screen implements AbstractViaConfigScreen {
    private static CompletableFuture<Void> latestProtocolSave;
    private final Screen parent;
    private EditBox protocolVersion;

    public ViaConfigScreen(Screen parent) {
        super(Component.translatable(TITLE_TRANSLATE_ID));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int entries = 0;

        this.addRenderableWidget(Button
            .builder(getClientSideText(), this::onClickClientSide)
            .bounds(calculatePosX(this.width, entries),
                calculatePosY(this.height, entries), 150, 20)
            .build());
        entries++;

        this.addRenderableWidget(Button
            .builder(getHideViaButtonText(), this::onHideViaButton)
            .bounds(calculatePosX(this.width, entries),
                calculatePosY(this.height, entries), 150, 20)
            .build());
        entries++;

        protocolVersion = new EditBox(this.font,
            calculatePosX(this.width, entries),
            calculatePosY(this.height, entries),
            150, 20, Component.translatable("gui.protocol_version_field.name"));
        entries++;

        protocolVersion.setFilter(ProtocolUtils::isStartOfProtocolText);
        protocolVersion.setResponder(this::onChangeVersionField);
        int clientSideVersion = ViaFabric.config.getClientSideVersion();
        protocolVersion.setValue(ProtocolUtils.getProtocolName(clientSideVersion));

        this.addRenderableWidget(protocolVersion);

        this.addRenderableWidget(Button
            .builder(CommonComponents.GUI_DONE, (it) -> onClose())
            .bounds(this.width / 2 - 100, this.height - 40, 200, 20)
            .build());
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
                        widget.setMessage(getClientSideText());
                    }
                    Minecraft.getInstance().setScreen(this);
                },
                Component.translatable("gui.enable_client_side.question"),
                Component.translatable("gui.enable_client_side.warning"),
                Component.translatable("gui.enable_client_side.enable"),
                Component.translatable("gui.cancel")
            ));
        } else {
            ViaFabric.config.setClientSideEnabled(false);
            ViaFabric.config.save();
        }
        widget.setMessage(getClientSideText());
    }

    @Override
    public void removed() {
        ViaFabric.config.save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private Component getClientSideText() {
        return ViaFabric.config.isClientSideEnabled() ?
            Component.translatable("gui.client_side.disable")
            : Component.translatable("gui.client_side.enable");
    }

    private Component getHideViaButtonText() {
        return ViaFabric.config.isHideButton() ?
            Component.translatable("gui.hide_via_button.disable") : Component.translatable("gui.hide_via_button.enable");
    }

    private void onHideViaButton(Button widget) {
        ViaFabric.config.setHideButton(!ViaFabric.config.isHideButton());
        ViaFabric.config.save();
        widget.setMessage(getHideViaButtonText());
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        protocolVersion.tick();
    }
}

