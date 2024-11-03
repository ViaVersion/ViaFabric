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
package com.viaversion.fabric.mc1213.gui;

import com.viaversion.fabric.common.config.AbstractViaConfigScreen;
import com.viaversion.fabric.common.util.ProtocolUtils;
import com.viaversion.fabric.mc1213.ViaFabric;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ViaConfigScreen extends Screen implements AbstractViaConfigScreen {
    private static CompletableFuture<Void> latestProtocolSave;
    private final Screen parent;
    private TextFieldWidget protocolVersion;

    public ViaConfigScreen(Screen parent) {
        super(Text.translatable(TITLE_TRANSLATE_ID));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int entries = 0;

        this.addDrawableChild(ButtonWidget
                .builder(getClientSideText(), this::onClickClientSide)
                .dimensions(calculatePosX(this.width, entries),
                        calculatePosY(this.height, entries), 150, 20)
                .build());
        entries++;

        this.addDrawableChild(ButtonWidget
                .builder(getHideViaButtonText(), this::onHideViaButton)
                .dimensions(calculatePosX(this.width, entries),
                        calculatePosY(this.height, entries), 150, 20)
                .build());
        entries++;

        protocolVersion = new TextFieldWidget(this.textRenderer,
                calculatePosX(this.width, entries),
                calculatePosY(this.height, entries),
                150, 20, Text.translatable("gui.protocol_version_field.name"));
        entries++;

        protocolVersion.setTextPredicate(ProtocolUtils::isStartOfProtocolText);
        protocolVersion.setChangedListener(this::onChangeVersionField);
        int clientSideVersion = ViaFabric.config.getClientSideVersion();
        protocolVersion.setText(ProtocolUtils.getProtocolName(clientSideVersion));

        this.addDrawableChild(protocolVersion);

        this.addDrawableChild(ButtonWidget
                .builder(ScreenTexts.DONE, (it) -> close())
                .dimensions(this.width / 2 - 100, this.height - 40, 200, 20)
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

        protocolVersion.setEditableColor(getProtocolTextColor(ProtocolVersion.getProtocol(newVersion), validProtocol));

        int finalNewVersion = newVersion;
        if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
        ViaFabric.config.setClientSideVersion(finalNewVersion);
        latestProtocolSave = latestProtocolSave.thenRunAsync(ViaFabric.config::save, ViaFabric.ASYNC_EXECUTOR);
    }

    private void onClickClientSide(ButtonWidget widget) {
        if (!ViaFabric.config.isClientSideEnabled()) {
            MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                    answer -> {
                        if (answer) {
                            ViaFabric.config.setClientSideEnabled(true);
                            ViaFabric.config.setClientSideVersion(-2); // AUTO
                            ViaFabric.config.save();
                            widget.setMessage(getClientSideText());
                        }
                        MinecraftClient.getInstance().setScreen(this);
                    },
                    Text.translatable("gui.enable_client_side.question"),
                    Text.translatable("gui.enable_client_side.warning"),
                    Text.translatable("gui.enable_client_side.enable"),
                    Text.translatable("gui.cancel")
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
    public void close() {
        this.client.setScreen(this.parent);
    }

    private Text getClientSideText() {
        return ViaFabric.config.isClientSideEnabled() ?
                Text.translatable("gui.client_side.disable")
                : Text.translatable("gui.client_side.enable");
    }

    private Text getHideViaButtonText() {
        return ViaFabric.config.isHideButton() ?
                Text.translatable("gui.hide_via_button.disable") : Text.translatable("gui.hide_via_button.enable");
    }

    private void onHideViaButton(ButtonWidget widget) {
        ViaFabric.config.setHideButton(!ViaFabric.config.isHideButton());
        ViaFabric.config.save();
        widget.setMessage(getHideViaButtonText());
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
