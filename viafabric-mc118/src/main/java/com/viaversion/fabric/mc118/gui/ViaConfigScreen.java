package com.viaversion.fabric.mc118.gui;

import com.viaversion.fabric.mc118.ViaFabric;
import com.viaversion.fabric.common.util.ProtocolUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ViaConfigScreen extends Screen {
    private static CompletableFuture<Void> latestProtocolSave;
    private final Screen parent;
    private TextFieldWidget protocolVersion;

    public ViaConfigScreen(Screen parent) {
        super(new TranslatableText("gui.viafabric_config.title"));
        this.parent = parent;
    }

    private static int getProtocolTextColor(boolean valid, boolean supported) {

        if (!valid) {
            return 0xff0000; // Red
        } else if (!supported) {
            return 0xFFA500; // Orange
        }
        return 0xE0E0E0; // Default
    }

    @Override
    protected void init() {
        int entries = 0;

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + entries % 2 * 160,
                this.height / 6 + 24 * (entries >> 1),
                150,
                20, getClientSideText(), this::onClickClientSide));
        entries++;

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + entries % 2 * 160,
                this.height / 6 + 24 * (entries >> 1),
                150,
                20, getHideViaButtonText(), this::onHideViaButton));
        entries++;

        protocolVersion = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 155 + entries % 2 * 160,
                this.height / 6 + 24 * (entries >> 1),
                150,
                20, new TranslatableText("gui.protocol_version_field.name"));
        entries++;

        protocolVersion.setTextPredicate(ProtocolUtils::isStartOfProtocolText);
        protocolVersion.setChangedListener(this::onChangeVersionField);
        int clientSideVersion = ViaFabric.config.getClientSideVersion();
        protocolVersion.setText(ProtocolUtils.getProtocolName(clientSideVersion));

        this.addDrawableChild(protocolVersion);

        //noinspection ConstantConditions
        if (entries % 2 == 1) {
            entries++;
        }

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (entries >> 1), 200, 20, ScreenTexts.DONE, (buttonWidget) -> this.client.setScreen(this.parent)));
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

        protocolVersion.setEditableColor(
                getProtocolTextColor(ProtocolUtils.isSupportedClientSide(newVersion),
                        validProtocol));

        int finalNewVersion = newVersion;
        if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
        ViaFabric.config.setClientSideVersion(finalNewVersion);
        latestProtocolSave = latestProtocolSave.thenRunAsync(ViaFabric.config::saveConfig, ViaFabric.ASYNC_EXECUTOR);
    }

    private void onClickClientSide(ButtonWidget widget) {
        if (!ViaFabric.config.isClientSideEnabled()) {
            MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                    answer -> {
                        if (answer) {
                            ViaFabric.config.setClientSideEnabled(true);
                            ViaFabric.config.setClientSideVersion(-2); // AUTO
                            ViaFabric.config.saveConfig();
                            widget.setMessage(getClientSideText());
                        }
                        MinecraftClient.getInstance().setScreen(this);
                    },
                    new TranslatableText("gui.enable_client_side.question"),
                    new TranslatableText("gui.enable_client_side.warning"),
                    new TranslatableText("gui.enable_client_side.enable"),
                    new TranslatableText("gui.cancel")
            ));
        } else {
            ViaFabric.config.setClientSideEnabled(false);
            ViaFabric.config.saveConfig();
        }
        widget.setMessage(getClientSideText());
    }

    @Override
    public void removed() {
        ViaFabric.config.saveConfig();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private TranslatableText getClientSideText() {
        return ViaFabric.config.isClientSideEnabled() ?
                new TranslatableText("gui.client_side.disable")
                : new TranslatableText("gui.client_side.enable");
    }

    private TranslatableText getHideViaButtonText() {
        return ViaFabric.config.isHideButton() ?
                new TranslatableText("gui.hide_via_button.disable") : new TranslatableText("gui.hide_via_button.enable");
    }

    private void onHideViaButton(ButtonWidget widget) {
        ViaFabric.config.setHideButton(!ViaFabric.config.isHideButton());
        ViaFabric.config.saveConfig();
        widget.setMessage(getHideViaButtonText());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        protocolVersion.tick();
    }
}

