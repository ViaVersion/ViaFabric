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

package com.github.creeper123123321.viafabric.protocol;

import com.google.common.base.Joiner;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class ClientSideReference extends Protocol {

    @Override
    protected void registerPackets() {
        // Plugin Message
        registerOutgoing(State.PLAY, 0x18, 0x18, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String channel = wrapper.passthrough(Type.STRING);

                        try {
                            new Identifier(channel);
                        } catch (InvalidIdentifierException ex) {
                            Via.getPlatform().getLogger().warning("Ignoring invalid custom payload identifier: " + ex.getMessage());
                            wrapper.set(Type.STRING, 0, "viafabric:invalid");
                            return;
                        }

                        if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                            String[] channels = new String(wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8).split("\0");

                            List<String> filteredChannels = new LinkedList<>();

                            for (String c : channels) {
                                try {
                                    new Identifier(c);
                                } catch (InvalidIdentifierException ex) {
                                    Via.getPlatform().getLogger().warning("Ignoring invalid custom payload identifier in " + channel + ": " + ex.getMessage());
                                    continue;
                                }
                                filteredChannels.add(c);
                            }

                            wrapper.write(Type.REMAINING_BYTES, Joiner.on('\0').join(filteredChannels).getBytes(StandardCharsets.UTF_8));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
    }
}
