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

package com.github.creeper123123321.viafabric.gui;

import com.github.creeper123123321.viafabric.util.VersionFormatFilter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.opengl.GL11;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtocolVersionFieldWidget extends TextFieldWidget {
	private final TextRenderer textRenderer;
	private boolean validProtocol;
	private boolean supportedProtocol;
	private Integer protocolVersion = null;
	private Consumer<Integer> changedListener;
	private boolean droppedDown;
	private List<String> elements;
	private int elementHeight;

	public ProtocolVersionFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height) {
		super(textRenderer, x, y, width, height, I18n.translate("gui.protocol_version_field.name"));
		if (width <= height) throw new IllegalArgumentException("width <= height");

		this.textRenderer = textRenderer;
		this.elementHeight = height;
		this.elements = ProtocolVersion.getProtocols().stream()
				                .filter(version -> isSupported(version.getId()))
				                .sorted(Comparator.comparingInt(version -> -version.getId()))
				                .map(ProtocolVersion::getName)
				                .collect(Collectors.toList());

		setTextPredicate(new VersionFormatFilter());
		setChangedListener((text) -> {
			setSuggestion(null);
			Integer newVersion = null;
			validProtocol = true;
			try {
				newVersion = Integer.parseInt(text);
			} catch (NumberFormatException e) {
				ProtocolVersion closest = ProtocolVersion.getClosest(text);
				if (closest != null) {
					newVersion = closest.getId();
				} else {
					validProtocol = false;
					List<String> completions = ProtocolVersion.getProtocols().stream()
							                           .map(ProtocolVersion::getName)
							                           .flatMap(str -> Stream.concat(
									                           Arrays.stream(str.split("-")),
									                           Arrays.stream(new String[]{str})
							                           ))
							                           .distinct()
							                           .filter(ver -> ver.startsWith(text))
							                           .collect(Collectors.toList());
					if (completions.size() == 1) {
						setSuggestion(completions.get(0).substring(text.length()));
					}
				}
			}

			supportedProtocol = newVersion != null && isSupported(newVersion);
			setEditableColor(getTextColor());

			if (!Objects.equals(newVersion, protocolVersion)) {
				protocolVersion = newVersion;
				if (changedListener != null) changedListener.accept(newVersion);
			}
		});
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float f) {
		if (!this.visible) return;

		this.width -= this.height + 1;
		super.renderButton(mouseX, mouseY, f);
		this.width += this.height + 1;

		fill(this.x + this.width - this.height - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0xFFA0A0A0);
		fill(this.x + this.width - this.height, this.y, this.x + this.width, this.y + this.height, 0xFF000000);

		String s = droppedDown ? "-" : "+";

		GL11.glPushMatrix();
		GL11.glTranslatef(this.x + this.width - this.height / 2 + 0.5f, this.y + (this.height - 9) / 2 - 0.5f, 0.0F);
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		this.textRenderer.draw(s, -textRenderer.getStringWidth(s) / 2, 0, 0xE0E0E0);
		GL11.glPopMatrix();

		if (droppedDown) {
			for (int k = 0; k < elements.size(); k++) {
				int top = this.y + this.height + this.elementHeight * k + 1;
				fill(this.x - 1, top - 1, this.x + this.width + 1, top + elementHeight + 1, 0xFFA0A0A0);
				fill(this.x, top, this.x + this.width, top + elementHeight, (mouseX >= this.x && mouseX < this.x + this.width && mouseY > top && mouseY <= top + elementHeight) ? 0xCC333333 : 0xAA000000);
				String text = elements.get(k);
				this.textRenderer.draw(text, this.x + this.width / 2 - textRenderer.getStringWidth(text) / 2, top + elementHeight / 2 - textRenderer.fontHeight / 2, 0xE0E0E0);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int i) {
		if (!this.visible) return false;

		if (mouseY >= this.y && mouseY < this.y + this.height && mouseX >= this.x + this.width - this.height && mouseX < this.x + this.width) {
			droppedDown = !droppedDown;
			return true;
		} else if (droppedDown && mouseY >= this.y + this.height && mouseY < this.y + this.height + this.elementHeight * elements.size() && mouseX >= this.x && mouseX < this.x + this.width) {
			int clicked = (int) ((mouseY - this.y - this.height) / this.elementHeight);
			setText(elements.get(clicked));
			droppedDown = false;
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, i);
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if (this.visible && droppedDown) {
			return mouseY >= this.y && mouseY < this.y + this.height + this.elementHeight * elements.size() && mouseX >= this.x && mouseX < this.x + this.width;
		} else {
			return super.isMouseOver(mouseX, mouseY);
		}
	}

	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolChangedListener(Consumer<Integer> consumer) {
		this.changedListener = consumer;
	}

	private int getTextColor() {
		if (!validProtocol) {
			return 0xFF0000; // Red
		} else if (!supportedProtocol) {
			return 0xFFA500; // Orange
		}
		return 0xE0E0E0; // Default
	}

	private boolean isSupported(int protocol) {
		return ProtocolRegistry.getProtocolPath(ProtocolRegistry.SERVER_PROTOCOL, protocol) != null || ProtocolRegistry.SERVER_PROTOCOL == protocol;
	}
}
