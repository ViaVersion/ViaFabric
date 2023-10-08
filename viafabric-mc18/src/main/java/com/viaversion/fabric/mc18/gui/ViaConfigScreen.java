package com.viaversion.fabric.mc18.gui;

import com.viaversion.fabric.common.config.AbstractViaConfigScreen;
import com.viaversion.fabric.mc18.ViaFabric;
import com.viaversion.fabric.common.util.ProtocolUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ViaConfigScreen extends Screen implements AbstractViaConfigScreen {
    private static CompletableFuture<Void> latestProtocolSave;
    private final Screen parent;
    private TextFieldWidget protocolVersion;
    private final Text title;

    public ViaConfigScreen(Screen parent) {
        super();
        title = new TranslatableText(TITLE_TRANSLATE_ID);
        this.parent = parent;
    }

    @Override
    public void init() {
        int entries = 0;

        this.buttons.add(new ListeneableButton("clientside".hashCode(), calculatePosX(this.width, entries),
                calculatePosY(this.height, entries),
                150,
                20, getClientSideText().asUnformattedString(), this::onClickClientSide));
        entries++;

        this.buttons.add(new ListeneableButton("hidevia".hashCode(), calculatePosX(this.width, entries),
                calculatePosY(this.height, entries),
                150,
                20, getHideViaButtonText().asUnformattedString(), this::onHideViaButton));
        entries++;

        protocolVersion = new TextFieldWidget("protover".hashCode(), textRenderer,
                calculatePosX(this.width, entries),
                calculatePosY(this.height, entries),
                150,
                20);
        protocolVersion.setText(new TranslatableText(VERSION_TRANSLATE_ID).asUnformattedString());
        entries++;

        protocolVersion.setTextPredicate(ProtocolUtils::isStartOfProtocolText);
        protocolVersion.setListener(new ChangedListener(this::onChangeVersionField));
        int clientSideVersion = ViaFabric.config.getClientSideVersion();
        protocolVersion.setText(ProtocolUtils.getProtocolName(clientSideVersion));
        onChangeVersionField(protocolVersion.getText());

        //this.children.add(protocolVersion);

        buttons.add(new ListeneableButton("done".hashCode(), this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("gui.done").asUnformattedString(),
                (buttonWidget) -> MinecraftClient.getInstance().setScreen(this.parent)));
    }

    private void onChangeVersionField(String text) {
        //protocolVersion.setSuggestion(null);
        int newVersion = ViaFabric.config.getClientSideVersion();

        Integer parsed = ProtocolUtils.parseProtocolId(text);
        boolean validProtocol;

        if (parsed != null) {
            newVersion = parsed;
            validProtocol = true;
        } else {
            validProtocol = false;
        }

        protocolVersion.setEditableColor(getProtocolTextColor(newVersion, validProtocol));

        int finalNewVersion = newVersion;
        if (latestProtocolSave == null) latestProtocolSave = CompletableFuture.completedFuture(null);
        ViaFabric.config.setClientSideVersion(finalNewVersion);
        latestProtocolSave = latestProtocolSave.thenRunAsync(ViaFabric.config::save, ViaFabric.ASYNC_EXECUTOR);
    }

    private void onClickClientSide(ButtonWidget widget) {
        if (!ViaFabric.config.isClientSideEnabled()) {
            MinecraftClient.getInstance().setScreen(new ConfirmScreen(
                    (answer, id) -> {
                        if (answer) {
                            ViaFabric.config.setClientSideEnabled(true);
                            ViaFabric.config.setClientSideVersion(-2); // AUTO
                            ViaFabric.config.save();
                            widget.message = getClientSideText().asUnformattedString();
                        }
                        MinecraftClient.getInstance().setScreen(this);
                    },
                    new TranslatableText("gui.enable_client_side.question").asUnformattedString(),
                    new TranslatableText("gui.enable_client_side.warning").asUnformattedString(),
                    new TranslatableText("gui.enable_client_side.enable").asUnformattedString(),
                    new TranslatableText("gui.cancel").asUnformattedString(),
                    "via anticheat consent".hashCode()
            ));
        } else {
            ViaFabric.config.setClientSideEnabled(false);
            ViaFabric.config.save();
        }
        widget.message = getClientSideText().asUnformattedString();
    }

    @Override
    public void removed() {
        ViaFabric.config.save();
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
        ViaFabric.config.save();
        widget.message = getHideViaButtonText().asUnformattedString();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        drawCenteredString(this.textRenderer, this.title.asUnformattedString(), this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, delta);
        protocolVersion.render();
    }

    @Override
    public void tick() {
        super.tick();
        protocolVersion.tick();
    }

    @Override
    protected void keyPressed(char character, int code) {
        super.keyPressed(character, code);
        protocolVersion.keyPressed(character, code);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        protocolVersion.mouseClicked(mouseX, mouseY, button);
    }
}

