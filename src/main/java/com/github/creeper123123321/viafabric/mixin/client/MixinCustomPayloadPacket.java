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

package com.github.creeper123123321.viafabric.mixin.client;

import net.fabricmc.fabric.impl.network.PacketTypes;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.myles.ViaVersion.api.Via;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Mixin(CustomPayloadS2CPacket.class)
public class MixinCustomPayloadPacket {

	@Inject(method = "read", at = @At(value = "HEAD", target = "Lnet/minecraft/network/Packet;read(Lnet/minecraft/util/PacketByteBuf;)V"))
	private void onRead(PacketByteBuf buf, CallbackInfo callbackInfo) throws IOException {
		try {
			int readerIndex = buf.readerIndex();

			Identifier identifier = buf.readIdentifier();

			buf.readerIndex(readerIndex);

			if (identifier.equals(PacketTypes.REGISTER) || identifier.equals(PacketTypes.UNREGISTER)) {
				Set<Identifier> identifiers = new HashSet();
				StringBuilder builder = new StringBuilder();

				while (buf.readerIndex() < buf.writerIndex()) {
					char c = (char) buf.readByte();
					if (c == 0 || buf.readableBytes() == 0) {
						String s = builder.toString();
						if (!s.isEmpty()) {
							try {
								identifiers.add(new Identifier(s));
							} catch (InvalidIdentifierException ex) {
								Via.getPlatform().getLogger().info("Ignoring invalid custom payload identifier in (un)register:\n" + ex.getMessage());
							}
						}

						builder = new StringBuilder();
					} else {
						builder.append(c);
					}
				}

				buf.clear();
				buf.writeIdentifier(identifier);

				boolean first = true;
				for (Identifier id : identifiers) {
					if (!first) {
						buf.writeByte(0);
					} else {
						first = false;
					}
					buf.writeBytes(id.toString().getBytes(StandardCharsets.US_ASCII));
				}
			}
		} catch (InvalidIdentifierException ex) {
			Via.getPlatform().getLogger().warning("Ignoring invalid custom payload identifier:\n" + ex.getMessage());
			buf.clear();
			buf.writeString("viafabric:invalid");
		}
	}
}
