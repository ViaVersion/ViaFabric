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

package com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10;

import com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10.chunks.ChunkPacketTransformer;
import com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10.metadata.MetadataRewriter;
import com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10.storage.*;
import com.google.common.base.Charsets;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.CustomIntType;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.CustomStringType;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.entities.Entity1_10Types;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.*;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.CustomByteType;
import us.myles.ViaVersion.api.type.types.VoidType;
import us.myles.ViaVersion.api.type.types.version.Types1_8;
import us.myles.ViaVersion.packets.State;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// Based on https://github.com/Gerrygames/ClientViaVersion
public class Protocol1_8TO1_7_6_10 extends Protocol {
    private static ValueReader xyzToPosition = (ValueReader<Position>) packetWrapper -> {
        long x = packetWrapper.read(Type.INT);
        long y = packetWrapper.read(Type.INT);
        long z = packetWrapper.read(Type.INT);
        return new Position(x, y, z);
    };

    @Override
    protected void registerPackets() {
        //Keep Alive
        this.registerOutgoing(State.PLAY, 0x00, 0x00, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);
            }
        });

        //Join Game
        this.registerOutgoing(State.PLAY, 0x01, 0x01, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT);  //Entiy Id
                map(Type.UNSIGNED_BYTE);  //Gamemode
                map(Type.BYTE);  //Dimension
                map(Type.UNSIGNED_BYTE);  //Difficulty
                map(Type.UNSIGNED_BYTE);  //Max players
                map(Type.STRING);  //Level Type
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BOOLEAN, false);  //Reduced Debug Info
                    }
                });
            }
        });

        //Chat Message
        this.registerOutgoing(State.PLAY, 0x02, 0x02, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);  //Chat Message
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BYTE, (byte) 0);  //Position (chat box)
                    }
                });
            }
        });

        //Entity Equipment
        this.registerOutgoing(State.PLAY, 0x04, 0x04, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.SHORT);  //Slot
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM);  //Item
            }
        });

        //Spawn Position
        this.registerOutgoing(State.PLAY, 0x05, 0x05, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION));  //Position
            }
        });

        //Update Health
        this.registerOutgoing(State.PLAY, 0x06, 0x06, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT);  //Health
                map(Type.SHORT, Type.VAR_INT);  //Food
                map(Type.FLOAT);  //Food Saturation
            }
        });

        //Player Position And Look
        this.registerOutgoing(State.PLAY, 0x08, 0x08, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE);  //x
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        double y = packetWrapper.read(Type.DOUBLE);
                        packetWrapper.write(Type.DOUBLE, y - 1.62);  //y - fixed value
                    }
                });
                map(Type.DOUBLE);  //z
                map(Type.FLOAT);  //pitch
                map(Type.FLOAT);  //yaw
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.read(Type.BOOLEAN);  //OnGround
                        packetWrapper.write(Type.BYTE, (byte) 0);  //BitMask
                    }
                });
            }
        });

        //Use Bed
        this.registerOutgoing(State.PLAY, 0x0A, 0x0A, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION));  //Position
            }
        });

        //Animation
        this.registerOutgoing(State.PLAY, 0x0B, 0x0B, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityId = packetWrapper.read(Type.VAR_INT);  //Entity Id
                        short animation = packetWrapper.read(Type.UNSIGNED_BYTE);  //Animation
                        packetWrapper.clearInputBuffer();
                        if (animation == 104 || animation == 105) {
                            packetWrapper.setId(0x1C);  //Entity Metadata
                            packetWrapper.write(Type.VAR_INT, entityId);  //Entity Id
                            packetWrapper.write(Type.UNSIGNED_BYTE, (short) 0);  //Index
                            packetWrapper.write(Type.UNSIGNED_BYTE, (short) 0);  //Type
                            packetWrapper.write(Type.BYTE, (byte) (animation == 104 ? 0x02 : 0x00));  //Value (sneaking/not sneaking)
                            packetWrapper.write(Type.UNSIGNED_BYTE, (short) 255);  //end
                        } else {
                            packetWrapper.write(Type.VAR_INT, entityId);  //Entity Id
                            packetWrapper.write(Type.UNSIGNED_BYTE, animation);  //Animation
                        }
                    }
                });
            }
        });

        //Spawn Player
        this.registerOutgoing(State.PLAY, 0x0C, 0x0C, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityId = packetWrapper.passthrough(Type.VAR_INT);  //Entity Id
                        UUID uuid = UUID.fromString(packetWrapper.read(Type.STRING));  //UUID
                        packetWrapper.write(Type.UUID, uuid);
                        String name = ChatColor.stripColor(packetWrapper.read(Type.STRING));  //Name
                        int dataCount = packetWrapper.read(Type.VAR_INT);  //DataCunt
                        ArrayList<Tablist.Property> properties = new ArrayList<>();
                        for (int i = 0; i < dataCount; i++) {
                            String key = packetWrapper.read(Type.STRING);  //Name
                            String value = packetWrapper.read(Type.STRING);  //Value
                            String signature = packetWrapper.read(Type.STRING);  //Signature
                            properties.add(new Tablist.Property(key, value, signature));
                        }
                        int x = packetWrapper.passthrough(Type.INT);  //x
                        int y = packetWrapper.passthrough(Type.INT);  //y
                        int z = packetWrapper.passthrough(Type.INT);  //z
                        byte yaw = packetWrapper.passthrough(Type.BYTE);  //yaw
                        byte pitch = packetWrapper.passthrough(Type.BYTE);  //pitch
                        short item = packetWrapper.passthrough(Type.SHORT);  //Item in hand
                        List<Metadata> metadata = packetWrapper.read(Types1_7_6_10.METADATA_LIST);  //Metadata
                        MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, metadata);
                        packetWrapper.write(Types1_8.METADATA_LIST, metadata);

                        Tablist tablist = packetWrapper.user().get(Tablist.class);
                        Tablist.TabListEntry entryByName = tablist.getTabListEntry(name);
                        if (entryByName == null && name.length() > 14)
                            entryByName = tablist.getTabListEntry(name.substring(0, 14));
                        Tablist.TabListEntry entryByUUID = tablist.getTabListEntry(uuid);

                        if (entryByName == null || entryByUUID == null) {
                            if (entryByName != null || entryByUUID != null) {
                                PacketWrapper remove = new PacketWrapper(0x38, null, packetWrapper.user());
                                remove.write(Type.VAR_INT, 4);
                                remove.write(Type.VAR_INT, 1);
                                remove.write(Type.UUID, entryByName == null ? entryByUUID.uuid : entryByName.uuid);
                                tablist.remove(entryByName == null ? entryByUUID : entryByName);
                                remove.send(Protocol1_8TO1_7_6_10.class);
                            }
                            PacketWrapper packetPlayerListItem = new PacketWrapper(0x38, null, packetWrapper.user());
                            Tablist.TabListEntry newentry = new Tablist.TabListEntry(name, uuid);
                            if (entryByName != null || entryByUUID != null) {
                                newentry.displayName = entryByUUID != null ? entryByUUID.displayName : entryByName.displayName;
                            }
                            newentry.properties = properties;
                            tablist.add(newentry);
                            packetPlayerListItem.write(Type.VAR_INT, 0);
                            packetPlayerListItem.write(Type.VAR_INT, 1);
                            packetPlayerListItem.write(Type.UUID, newentry.uuid);
                            packetPlayerListItem.write(Type.STRING, newentry.name);
                            packetPlayerListItem.write(Type.VAR_INT, dataCount);
                            for (Tablist.Property property : newentry.properties) {
                                packetPlayerListItem.write(Type.STRING, property.name);
                                packetPlayerListItem.write(Type.STRING, property.value);
                                packetPlayerListItem.write(Type.BOOLEAN, property.signature != null);
                                if (property.signature != null)
                                    packetPlayerListItem.write(Type.STRING, property.signature);
                            }
                            packetPlayerListItem.write(Type.VAR_INT, 0);
                            packetPlayerListItem.write(Type.VAR_INT, 0);
                            packetPlayerListItem.write(Type.BOOLEAN, newentry.displayName != null);
                            if (newentry.displayName != null) {
                                packetPlayerListItem.write(Type.STRING, newentry.displayName);
                            }
                            packetPlayerListItem.send(Protocol1_8TO1_7_6_10.class);

                            packetWrapper.cancel();

                            final PacketWrapper delayedPacket = new PacketWrapper(0x0C, null, packetWrapper.user());
                            delayedPacket.write(Type.VAR_INT, entityId);
                            delayedPacket.write(Type.UUID, uuid);
                            delayedPacket.write(Type.INT, x);
                            delayedPacket.write(Type.INT, y);
                            delayedPacket.write(Type.INT, z);
                            delayedPacket.write(Type.BYTE, yaw);
                            delayedPacket.write(Type.BYTE, pitch);
                            delayedPacket.write(Type.SHORT, item);
                            delayedPacket.write(Types1_8.METADATA_LIST, metadata);

                            Via.getPlatform().runSync(() -> {
                                try {
                                    delayedPacket.send(Protocol1_8TO1_7_6_10.class);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }, 1L);
                        } else {
                            entryByUUID.properties = properties;
                        }
                    }
                });
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityID = packetWrapper.get(Type.VAR_INT, 0);
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.PLAYER);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
            }
        });

        //Collect Item
        this.registerOutgoing(State.PLAY, 0x0D, 0x0D, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Collected Entity ID
                map(Type.INT, Type.VAR_INT);  //Collector Entity ID
            }
        });

        //Spawn Object
        this.registerOutgoing(State.PLAY, 0x0E, 0x0E, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.BYTE);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        byte type = packetWrapper.get(Type.BYTE, 0);
                        int x = packetWrapper.get(Type.INT, 0);
                        int y = packetWrapper.get(Type.INT, 1);
                        int z = packetWrapper.get(Type.INT, 2);
                        byte yaw = packetWrapper.get(Type.BYTE, 2);
                        int data = packetWrapper.get(Type.INT, 3);

                        if (type == 71) {
                            switch (data) {
                                case 0:
                                    z += 32;
                                    yaw = 0;
                                    break;
                                case 1:
                                    x -= 32;
                                    yaw = (byte) 64;
                                    break;
                                case 2:
                                    z -= 32;
                                    yaw = (byte) 128;
                                    break;
                                case 3:
                                    x += 32;
                                    yaw = (byte) 192;
                                    break;
                            }
                        }

                        if (type == 70) {
                            int id = data;
                            int metadata = data >> 16;
                            data = id | metadata << 12;
                        }

                        if (type == 50 || type == 70 || type == 74) y -= 16;

                        packetWrapper.set(Type.INT, 0, x);
                        packetWrapper.set(Type.INT, 1, y);
                        packetWrapper.set(Type.INT, 2, z);
                        packetWrapper.set(Type.BYTE, 2, yaw);
                        packetWrapper.set(Type.INT, 3, data);
                    }
                });
                handler(new PacketHandler() {
                    @Override
                    public void handle(final PacketWrapper packetWrapper) throws Exception {
                        final int entityID = packetWrapper.get(Type.VAR_INT, 0);
                        final int typeID = packetWrapper.get(Type.BYTE, 0);
                        final EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        final Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(typeID, true);
                        tracker.getClientEntityTypes().put(entityID, type);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
            }
        });

        //Spawn Mob
        this.registerOutgoing(State.PLAY, 0x0F, 0x0F, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.UNSIGNED_BYTE);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.SHORT);
                map(Type.SHORT);
                map(Type.SHORT);
                map(Types1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityID = packetWrapper.get(Type.VAR_INT, 0);
                        int typeID = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.getTypeFromId(typeID, false));
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
                handler(new PacketHandler() {
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker tracker = wrapper.user().get(EntityTracker.class);
                        if (tracker.getClientEntityTypes().containsKey(entityID)) {
                            MetadataRewriter.transform(tracker.getClientEntityTypes().get(entityID), metadataList);
                        } else {
                            wrapper.cancel();
                        }
                    }
                });

            }
        });

        //Spawn Painting
        this.registerOutgoing(State.PLAY, 0x10, 0x10, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);  //Entity Id
                map(Type.STRING);  //Title
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION));  //Position
                map(Type.INT, Type.BYTE);  //Rotation
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityID = packetWrapper.get(Type.VAR_INT, 0);
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.PAINTING);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
            }
        });

        //Spawn Experience Orb
        this.registerOutgoing(State.PLAY, 0x11, 0x11, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.SHORT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityID = packetWrapper.get(Type.VAR_INT, 0);
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
            }
        });

        //Entity Velocity
        this.registerOutgoing(State.PLAY, 0x12, 0x12, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.SHORT);  //velX
                map(Type.SHORT);  //velY
                map(Type.SHORT);  //velZ
            }
        });

        //Destroy Entities
        this.registerOutgoing(State.PLAY, 0x13, 0x13, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int amount = packetWrapper.read(Type.BYTE);
                        CustomIntType customIntType = new CustomIntType(amount);
                        Integer[] entityIds = packetWrapper.read(customIntType);
                        packetWrapper.write(Type.VAR_INT_ARRAY, entityIds);
                    }
                });  //Entity Id Array
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        for (int entityId : packetWrapper.get(Type.VAR_INT_ARRAY, 0)) tracker.removeEntity(entityId);
                    }
                });
            }
        });

        //Entity
        this.registerOutgoing(State.PLAY, 0x14, 0x14, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
            }
        });

        //Entity Relative Move
        this.registerOutgoing(State.PLAY, 0x15, 0x15, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.BYTE);  //x
                map(Type.BYTE);  //y
                map(Type.BYTE);  //z
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BOOLEAN, true);  //OnGround
                    }
                });
            }
        });

        //Entity Look
        this.registerOutgoing(State.PLAY, 0x16, 0x16, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.BYTE);  //yaw
                map(Type.BYTE);  //pitch
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BOOLEAN, true);  //OnGround
                    }
                });
            }
        });

        //Entity Look and Relative Move
        this.registerOutgoing(State.PLAY, 0x17, 0x17, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.BYTE);  //x
                map(Type.BYTE);  //y
                map(Type.BYTE);  //z
                map(Type.BYTE);  //yaw
                map(Type.BYTE);  //pitch
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BOOLEAN, true);  //OnGround
                    }
                });
            }
        });

        //Entity Teleport
        this.registerOutgoing(State.PLAY, 0x18, 0x18, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.INT);  //x
                map(Type.INT);  //y
                map(Type.INT);  //z
                map(Type.BYTE);  //yaw
                map(Type.BYTE);  //pitch
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BOOLEAN, true);  //OnGround
                    }
                });
            }
        });

        //Entity Head Look
        this.registerOutgoing(State.PLAY, 0x19, 0x19, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.BYTE);  //Head yaw
            }
        });

        //Entity MetadataType
        this.registerOutgoing(State.PLAY, 0x1C, 0x1C, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Types1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST);  //MetadataType
                handler(new PacketHandler() {
                    public void handle(PacketWrapper wrapper) throws Exception {
                        List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                        int entityID = wrapper.get(Type.VAR_INT, 0);
                        EntityTracker tracker = wrapper.user().get(EntityTracker.class);
                        if (tracker.getClientEntityTypes().containsKey(entityID)) {
                            MetadataRewriter.transform(tracker.getClientEntityTypes().get(entityID), metadataList);
                            if (metadataList.isEmpty()) wrapper.cancel();
                        } else {
                            tracker.addMetadataToBuffer(entityID, metadataList);
                            wrapper.cancel();
                        }
                    }
                });
            }
        });

        //Entity Effect
        this.registerOutgoing(State.PLAY, 0x1D, 0x1D, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.BYTE);  //Effect Id
                map(Type.BYTE);  //Amplifier
                map(Type.SHORT, Type.VAR_INT);  //Duration
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BOOLEAN, false);
                    }
                });  //Hide Particles
            }
        });

        //Remove Entity Effect
        this.registerOutgoing(State.PLAY, 0x1E, 0x1E, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                map(Type.BYTE);  //Effect Id
            }
        });

        //Set Experience
        this.registerOutgoing(State.PLAY, 0x1F, 0x1F, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT);  //Experience bar
                map(Type.SHORT, Type.VAR_INT);  //Level
                map(Type.SHORT, Type.VAR_INT);  //Total Experience
            }
        });

        //Entity Properties
        this.registerOutgoing(State.PLAY, 0x20, 0x20, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT);  //Entity Id
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int amount = packetWrapper.read(Type.INT);
                        packetWrapper.write(Type.INT, amount);
                        for (int i = 0; i < amount; i++) {
                            packetWrapper.passthrough(Type.STRING);
                            packetWrapper.passthrough(Type.DOUBLE);
                            int modifierlength = packetWrapper.read(Type.SHORT);
                            packetWrapper.write(Type.VAR_INT, modifierlength);
                            for (int j = 0; j < modifierlength; j++) {
                                packetWrapper.passthrough(Type.UUID);
                                packetWrapper.passthrough(Type.DOUBLE);
                                packetWrapper.passthrough(Type.BYTE);
                            }
                        }
                    }
                });

            }
        });

        //Chunk Data
        this.registerOutgoing(State.PLAY, 0x21, 0x21, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        ChunkPacketTransformer.transformChunk(packetWrapper);
                    }
                });
            }
        });

        //Multi Block Change
        this.registerOutgoing(State.PLAY, 0x22, 0x22, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        ChunkPacketTransformer.transformMultiBlockChange(packetWrapper);
                    }
                });
            }
        });

        //Block Change
        this.registerOutgoing(State.PLAY, 0x23, 0x23, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    long x = packetWrapper.read(Type.INT);
                    long y = packetWrapper.read(Type.UNSIGNED_BYTE);
                    long z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION));  //Position
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int blockId = packetWrapper.read(Type.VAR_INT);
                        int meta = packetWrapper.read(Type.UNSIGNED_BYTE);
                        packetWrapper.write(Type.VAR_INT, blockId << 4 | (meta & 15));
                    }
                });  //Block Data
            }
        });

        //Block Action
        this.registerOutgoing(State.PLAY, 0x24, 0x24, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    long x = packetWrapper.read(Type.INT);
                    long y = packetWrapper.read(Type.SHORT);
                    long z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION));  //Position
                map(Type.UNSIGNED_BYTE);
                map(Type.UNSIGNED_BYTE);
                map(Type.VAR_INT);
            }
        });

        //Block Break Animation
        this.registerOutgoing(State.PLAY, 0x25, 0x25, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);  //Entity Id
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION));  //Position
                map(Type.BYTE);  //Progress
            }
        });

        //Map Chunk Bulk
        this.registerOutgoing(State.PLAY, 0x26, 0x26, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        ChunkPacketTransformer.transformChunkBulk(packetWrapper);
                    }
                });
            }
        });

        //Effect
        this.registerOutgoing(State.PLAY, 0x28, 0x28, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int effectId = packetWrapper.read(Type.INT);
                        long x = packetWrapper.read(Type.INT);
                        long y = packetWrapper.read(Type.BYTE);
                        long z = packetWrapper.read(Type.INT);
                        int data = packetWrapper.read(Type.INT);
                        boolean disableRelativeVolume = packetWrapper.read(Type.BOOLEAN);

                        if (effectId == 2006) {
                            packetWrapper.cancel();
                        } else {
                            packetWrapper.write(Type.INT, effectId);
                            packetWrapper.write(Type.POSITION, new Position(x, y, z));
                            packetWrapper.write(Type.INT, data);
                            packetWrapper.write(Type.BOOLEAN, disableRelativeVolume);
                        }
                    }
                });
            }
        });

        //Particle
        this.registerOutgoing(State.PLAY, 0x2A, 0x2A, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String[] parts = packetWrapper.read(Type.STRING).split("_");
                        Particle particle = Particle.find(parts[0]);
                        if (particle == null) particle = Particle.CRIT;
                        packetWrapper.write(Type.INT, particle.ordinal());
                        packetWrapper.write(Type.BOOLEAN, false);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.FLOAT);
                        packetWrapper.passthrough(Type.INT);
                        for (int i = 0; i < particle.extra; ++i) {
                            int toWrite = 0;
                            if (parts.length - 1 > i) {
                                try {
                                    toWrite = Integer.parseInt(parts[i + 1]);
                                    if (particle.extra == 1 && parts.length == 3) {
                                        ++i;
                                        toWrite |= Integer.parseInt(parts[i + 1]) << 12;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                            packetWrapper.write(Type.VAR_INT, toWrite);
                        }
                    }
                });
            }
        });

        //Spawn Global Entity
        this.registerOutgoing(State.PLAY, 0x2C, 0x2C, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.BYTE);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int entityID = packetWrapper.get(Type.VAR_INT, 0);
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.LIGHTNING);
                        tracker.sendMetadataBuffer(entityID);
                    }
                });
            }
        });

        //Open Window
        this.registerOutgoing(State.PLAY, 0x2D, 0x2D, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        short windowId = packetWrapper.read(Type.UNSIGNED_BYTE);
                        packetWrapper.write(Type.UNSIGNED_BYTE, windowId);
                        short windowType = packetWrapper.read(Type.UNSIGNED_BYTE);
                        packetWrapper.user().get(Windows.class).types.put(windowId, windowType);
                        packetWrapper.write(Type.STRING, getInventoryString(windowType));  //Inventory Type
                        String title = packetWrapper.read(Type.STRING);  //Title
                        short slots = packetWrapper.read(Type.UNSIGNED_BYTE);
                        boolean useProvidedWindowTitle = packetWrapper.read(Type.BOOLEAN);  //Use provided window title
                        if (useProvidedWindowTitle) {
                            title = "{\"text\": \"" + title + "\"}";
                        } else {
                            title = "{\"translate\": \"" + title + "\"}";
                        }
                        packetWrapper.write(Type.STRING, title);  //Window title
                        packetWrapper.write(Type.UNSIGNED_BYTE, slots);
                        if (packetWrapper.get(Type.UNSIGNED_BYTE, 0) == 11)
                            packetWrapper.passthrough(Type.INT);  //Entity Id
                    }
                });
            }
        });

        //Set Slot
        this.registerOutgoing(State.PLAY, 0x2F, 0x2F, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        short windowId = packetWrapper.read(Type.BYTE);  //Window Id
                        short windowType = packetWrapper.user().get(Windows.class).get(windowId);
                        packetWrapper.write(Type.BYTE, (byte) windowId);
                        short slot = packetWrapper.read(Type.SHORT);
                        if (windowType == 4 && slot >= 1) slot += 1;
                        packetWrapper.write(Type.SHORT, slot);  //Slot
                    }
                });
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM);  //Item
            }
        });

        //Window Items
        this.registerOutgoing(State.PLAY, 0x30, 0x30, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        short windowId = packetWrapper.passthrough(Type.UNSIGNED_BYTE);  //Window Id
                        short windowType = packetWrapper.user().get(Windows.class).get(windowId);
                        Item[] items = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY);
                        if (windowType == 4) {
                            Item[] old = items;
                            items = new Item[old.length + 1];
                            items[0] = old[0];
                            System.arraycopy(old, 1, items, 2, old.length - 1);
                            items[1] = new Item((short) 351, (byte) 3, (short) 4, null);
                        }
                        packetWrapper.write(Type.ITEM_ARRAY, items);  //Items
                    }
                });
            }
        });

        //Update Sign
        this.registerOutgoing(State.PLAY, 0x33, 0x33, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    long x = packetWrapper.read(Type.INT);
                    long y = packetWrapper.read(Type.SHORT);
                    long z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION));  //Position
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        for (int i = 0; i < 4; i++)
                            packetWrapper.write(Type.STRING, "{\"text\": \"" + packetWrapper.read(Type.STRING) + "\"}");
                    }
                });
            }
        });

        //Map
        this.registerOutgoing(State.PLAY, 0x34, 0x34, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int id = packetWrapper.get(Type.VAR_INT, 0);
                        int length = packetWrapper.read(Type.SHORT);
                        byte[] data = packetWrapper.read(new CustomByteType(length));

                        MapStorage mapStorage = packetWrapper.user().get(MapStorage.class);
                        MapStorage.MapData mapData = mapStorage.getMapData(id);
                        if (mapData == null) mapStorage.putMapData(id, mapData = new MapStorage.MapData());

                        if (data[0] == 1) {
                            int count = (data.length - 1) / 3;
                            mapData.mapIcons = new MapStorage.MapIcon[count];

                            for (int i = 0; i < count; i++) {
                                mapData.mapIcons[i] = new MapStorage.MapIcon((byte) (data[i * 3 + 1] >> 4), (byte) (data[i * 3 + 1] & 0xF), data[i * 3 + 2], data[i * 3 + 3]);
                            }
                        } else if (data[0] == 2) {
                            mapData.scale = data[1];
                        }

                        packetWrapper.write(Type.BYTE, mapData.scale);
                        packetWrapper.write(Type.VAR_INT, mapData.mapIcons.length);
                        for (MapStorage.MapIcon mapIcon : mapData.mapIcons) {
                            packetWrapper.write(Type.BYTE, (byte) (mapIcon.direction << 4 | mapIcon.type & 0xF));
                            packetWrapper.write(Type.BYTE, mapIcon.x);
                            packetWrapper.write(Type.BYTE, mapIcon.z);
                        }

                        if (data[0] == 0) {
                            byte x = data[1];
                            byte z = data[2];
                            int rows = data.length - 3;

                            packetWrapper.write(Type.BYTE, (byte) 1);
                            packetWrapper.write(Type.BYTE, (byte) rows);
                            packetWrapper.write(Type.BYTE, x);
                            packetWrapper.write(Type.BYTE, z);

                            Byte[] newData = new Byte[rows];

                            for (int i = 0; i < rows; i++) {
                                newData[i] = data[i + 3];
                            }

                            packetWrapper.write(Type.BYTE_ARRAY, newData);
                        } else {
                            packetWrapper.write(Type.BYTE, (byte) 0);
                        }
                    }
                });
            }
        });

        //Update Block Entity
        this.registerOutgoing(State.PLAY, 0x35, 0x35, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    long x = packetWrapper.read(Type.INT);
                    long y = packetWrapper.read(Type.SHORT);
                    long z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION));  //Position
                map(Type.UNSIGNED_BYTE);  //Action
                map(Types1_7_6_10.COMPRESSED_NBT, Type.NBT);
            }
        });

        //Open Sign Editor
        this.registerOutgoing(State.PLAY, 0x36, 0x36, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION));  //Position
            }
        });

        //Player List Item
        this.registerOutgoing(State.PLAY, 0x38, 0x38, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String name = packetWrapper.read(Type.STRING);
                        String displayName = null;
                        boolean online = packetWrapper.read(Type.BOOLEAN);
                        short ping = packetWrapper.read(Type.SHORT);

                        Tablist tablist = packetWrapper.user().get(Tablist.class);

                        Tablist.TabListEntry entry = tablist.getTabListEntry(name);

                        if (!online && entry != null) {
                            packetWrapper.write(Type.VAR_INT, 4);
                            packetWrapper.write(Type.VAR_INT, 1);
                            packetWrapper.write(Type.UUID, entry.uuid);
                            tablist.remove(entry);
                        } else if (online && entry == null) {
                            entry = new Tablist.TabListEntry(name, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)));
                            entry.displayName = displayName;
                            tablist.add(entry);
                            packetWrapper.write(Type.VAR_INT, 0); // Add
                            packetWrapper.write(Type.VAR_INT, 1); // Entries
                            packetWrapper.write(Type.UUID, entry.uuid);
                            packetWrapper.write(Type.STRING, entry.name);
                            packetWrapper.write(Type.VAR_INT, entry.properties.size());
                            for (Tablist.Property property : entry.properties) {
                                packetWrapper.write(Type.STRING, property.name);
                                packetWrapper.write(Type.STRING, property.value);
                                packetWrapper.write(Type.BOOLEAN, property.signature != null);
                                if (property.signature != null) packetWrapper.write(Type.STRING, property.signature);
                            }
                            packetWrapper.write(Type.VAR_INT, 0);
                            packetWrapper.write(Type.VAR_INT, (int) ping);
                            packetWrapper.write(Type.BOOLEAN, entry.displayName != null);
                            if (entry.displayName != null) {
                                packetWrapper.write(Type.STRING, entry.displayName);
                            }
                        } else if (online && Tablist.shouldUpdateDisplayName(entry.displayName, displayName)) {
                            entry.displayName = displayName;
                            packetWrapper.write(Type.VAR_INT, 3);
                            packetWrapper.write(Type.VAR_INT, 1);
                            packetWrapper.write(Type.UUID, entry.uuid);
                            packetWrapper.write(Type.BOOLEAN, entry.displayName != null);
                            if (entry.displayName != null) {
                                packetWrapper.write(Type.STRING, entry.displayName);
                            }
                        } else if (online) {
                            entry.ping = ping;
                            packetWrapper.write(Type.VAR_INT, 2); // Update ping
                            packetWrapper.write(Type.VAR_INT, 1); // Entries
                            packetWrapper.write(Type.UUID, entry.uuid);
                            packetWrapper.write(Type.VAR_INT, (int) ping);
                        } else {
                            packetWrapper.write(Type.VAR_INT, 0);
                            packetWrapper.write(Type.VAR_INT, 0);
                        }
                    }
                });
            }
        });

        //Scoreboard Objective
        this.registerOutgoing(State.PLAY, 0x3B, 0x3B, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String name = packetWrapper.passthrough(Type.STRING);
                        String value = packetWrapper.read(Type.STRING);
                        byte mode = packetWrapper.read(Type.BYTE);

                        packetWrapper.write(Type.BYTE, mode);
                        if (mode == 0 || mode == 2) {
                            packetWrapper.write(Type.STRING, value);
                            packetWrapper.write(Type.STRING, "integer");
                        }
                    }
                });
            }
        });

        //Update Score
        this.registerOutgoing(State.PLAY, 0x3C, 0x3C, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String name = packetWrapper.passthrough(Type.STRING);
                        byte mode = packetWrapper.passthrough(Type.BYTE);
                        if (mode != 1) {
                            String objective = packetWrapper.passthrough(Type.STRING);
                            packetWrapper.user().get(Scoreboard.class).put(name, objective);
                            packetWrapper.write(Type.VAR_INT, packetWrapper.read(Type.INT));
                        } else {
                            String objective = packetWrapper.user().get(Scoreboard.class).get(name);
                            packetWrapper.write(Type.STRING, objective);
                        }
                    }
                });
            }
        });

        //Scoreboard Teams
        this.registerOutgoing(State.PLAY, 0x3E, 0x3E, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        byte mode = packetWrapper.read(Type.BYTE);
                        packetWrapper.write(Type.BYTE, mode);
                        if (mode == 0 || mode == 2) {
                            packetWrapper.passthrough(Type.STRING);
                            packetWrapper.passthrough(Type.STRING);
                            packetWrapper.passthrough(Type.STRING);
                            packetWrapper.passthrough(Type.BYTE);
                            packetWrapper.write(Type.STRING, "always");
                            packetWrapper.write(Type.BYTE, (byte) 0);
                        }
                        if (mode == 0 || mode == 3 || mode == 4) {
                            int count = packetWrapper.read(Type.SHORT);
                            CustomStringType type = new CustomStringType(count);
                            String[] entries = packetWrapper.read(type);
                            packetWrapper.write(Type.STRING_ARRAY, entries);
                        }
                    }
                });
            }
        });

        //Custom Payload
        this.registerOutgoing(State.PLAY, 0x3F, 0x3F, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String channel = packetWrapper.get(Type.STRING, 0);
                        short length = packetWrapper.read(Type.SHORT);
                        if (channel.equals("MC|Brand")) {
                            byte[] data = packetWrapper.read(new CustomByteType((int) length));
                            String brand = new String(data, StandardCharsets.UTF_8);
                            packetWrapper.write(Type.STRING, brand);
                        } else if (channel.equals("MC|AdvCdm")) {
                            byte type = packetWrapper.passthrough(Type.BYTE);
                            if (type == 0) {
                                packetWrapper.passthrough(Type.INT);
                                packetWrapper.passthrough(Type.INT);
                                packetWrapper.passthrough(Type.INT);
                                packetWrapper.passthrough(Type.STRING);
                                packetWrapper.passthrough(Type.BOOLEAN);
                            } else if (type == 1) {
                                packetWrapper.passthrough(Type.INT);
                                packetWrapper.passthrough(Type.STRING);
                                packetWrapper.passthrough(Type.BOOLEAN);
                            }
                            packetWrapper.write(Type.BYTE, (byte) 1);
                        }
                    }
                });
            }
        });

        //Keep Alive
        this.registerIncoming(State.PLAY, 0x00, 0x00, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.INT);
            }
        });

        //Use Entity
        this.registerIncoming(State.PLAY, 0x02, 0x02, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.INT);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int mode = packetWrapper.read(Type.VAR_INT);
                        if (mode == 2) {
                            packetWrapper.write(Type.BYTE, (byte) 0);
                            packetWrapper.read(Type.FLOAT);
                            packetWrapper.read(Type.FLOAT);
                            packetWrapper.read(Type.FLOAT);
                        } else {
                            packetWrapper.write(Type.BYTE, (byte) mode);
                        }
                    }
                });
            }
        });

        //Player Position
        this.registerIncoming(State.PLAY, 0x04, 0x04, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE);  //X
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        double feetY = packetWrapper.passthrough(Type.DOUBLE);
                        packetWrapper.write(Type.DOUBLE, feetY + 1.62);  //HeadY
                    }
                });
                map(Type.DOUBLE);  //Z
                map(Type.BOOLEAN);  //OnGround
            }
        });

        //Player Position And Look
        this.registerIncoming(State.PLAY, 0x06, 0x06, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE);  //X
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        double feetY = packetWrapper.passthrough(Type.DOUBLE);
                        packetWrapper.write(Type.DOUBLE, feetY + 1.62);  //HeadY
                    }
                });
                map(Type.DOUBLE);  //Z
                map(Type.FLOAT);  //Yaw
                map(Type.FLOAT);  //Pitch
                map(Type.BOOLEAN);  //OnGround
            }
        });

        //Player Digging
        this.registerIncoming(State.PLAY, 0x07, 0x07, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE);  //Status
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        Position pos = packetWrapper.read(Type.POSITION);
                        packetWrapper.write(Type.INT, pos.getX().intValue());
                        packetWrapper.write(Type.UNSIGNED_BYTE, pos.getY().shortValue());
                        packetWrapper.write(Type.INT, pos.getZ().intValue());
                    }
                });
                map(Type.BYTE);  //Face
            }
        });

        //Player Block Placement
        this.registerIncoming(State.PLAY, 0x08, 0x08, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int x;
                        short y;
                        int z;
                        if (packetWrapper.isReadable(Type.POSITION, 0)) {
                            Position pos = packetWrapper.read(Type.POSITION);  //Position
                            x = pos.getX().intValue();
                            y = pos.getY().shortValue();
                            z = pos.getZ().intValue();
                        } else {
                            Long pos = packetWrapper.read(Type.LONG);  //Position
                            x = (int) (pos >> 38);
                            y = (short) (pos >> 26 & 4095L);
                            z = (int) (pos << 38 >> 38);

                        }
                        packetWrapper.write(Type.INT, x);
                        packetWrapper.write(Type.UNSIGNED_BYTE, y);
                        packetWrapper.write(Type.INT, z);
                        byte direction = packetWrapper.passthrough(Type.BYTE);  //Direction
                        VoidType voidType = new VoidType();
                        if (packetWrapper.isReadable(voidType, 0)) packetWrapper.read(voidType);
                        Item item = packetWrapper.read(Type.ITEM);
                        packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);

                        if (isPlayerInsideBlock(x, y, z, direction) && !isPlaceable(item.getId()))
                            packetWrapper.cancel();

                        for (int i = 0; i < 3; i++) {
                            if (packetWrapper.isReadable(Type.BYTE, 0)) {
                                packetWrapper.passthrough(Type.BYTE);
                            } else {
                                short cursor = packetWrapper.read(Type.UNSIGNED_BYTE);
                                packetWrapper.write(Type.BYTE, (byte) cursor);
                            }
                        }
                    }
                });
            }
        });

        //Animation
        this.registerIncoming(State.PLAY, 0x0A, 0x0A, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.INT, 0);  //Entity Id, hopefully 0 is ok
                        packetWrapper.write(Type.BYTE, (byte) 1);  //Animation
                    }
                });
            }
        });

        //Entity Action
        this.registerIncoming(State.PLAY, 0x0B, 0x0B, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.INT);  //Entity Id
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BYTE, (byte) (packetWrapper.read(Type.VAR_INT) + 1));
                    }
                });  //Action Id
                map(Type.VAR_INT, Type.INT);  //Action Paramter
            }
        });

        //Steer Vehicle
        this.registerIncoming(State.PLAY, 0x0C, 0x0C, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT);  //Sideways
                map(Type.FLOAT);  //Forwards
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        short flags = packetWrapper.read(Type.UNSIGNED_BYTE);
                        packetWrapper.write(Type.BOOLEAN, (flags & 1) == 1);  //Jump
                        packetWrapper.write(Type.BOOLEAN, (flags & 2) == 2);  //Unmount
                    }
                });
            }
        });

        //Click Window
        this.registerIncoming(State.PLAY, 0x0E, 0x0E, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        short windowId = packetWrapper.read(Type.UNSIGNED_BYTE);  //Window Id
                        packetWrapper.write(Type.BYTE, (byte) windowId);
                        short windowType = packetWrapper.user().get(Windows.class).get(windowId);
                        short slot = packetWrapper.read(Type.SHORT);
                        if (windowType == 4) {
                            if (slot == 1) {
                                packetWrapper.cancel();
                            } else if (slot > 1) {
                                slot -= 1;
                            }
                        }
                        packetWrapper.write(Type.SHORT, slot);  //Slot
                    }
                });
                map(Type.BYTE);  //Button
                map(Type.SHORT);  //Action Number
                map(Type.BYTE);  //Mode
                map(Type.ITEM, Types1_7_6_10.COMPRESSED_NBT_ITEM);
            }
        });

        //Creative Inventory Action
        this.registerIncoming(State.PLAY, 0x10, 0x10, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT);  //Slot
                map(Type.ITEM, Types1_7_6_10.COMPRESSED_NBT_ITEM);  //Item
            }
        });

        //Update Sign
        this.registerIncoming(State.PLAY, 0x12, 0x12, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        Position pos = packetWrapper.read(Type.POSITION);
                        packetWrapper.write(Type.INT, pos.getX().intValue());
                        packetWrapper.write(Type.SHORT, (short) pos.getY().intValue());
                        packetWrapper.write(Type.INT, pos.getZ().intValue());

                        for (int i = 0; i < 4; i++)
                            packetWrapper.write(Type.STRING, TextComponent.toLegacyText(ComponentSerializer.parse(packetWrapper.read(Type.STRING))));
                    }
                });
            }
        });

        //Tab-Complete
        this.registerIncoming(State.PLAY, 0x14, 0x14, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        String text = packetWrapper.read(Type.STRING);
                        packetWrapper.clearInputBuffer();
                        packetWrapper.write(Type.STRING, text);
                    }
                });
            }
        });

        //Client Settings
        this.registerIncoming(State.PLAY, 0x15, 0x15, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.BOOLEAN);
                create(new ValueCreator() {
                    @Override
                    public void write(PacketWrapper packetWrapper) throws Exception {
                        packetWrapper.write(Type.BYTE, (byte) 0);
                    }
                });
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        short flags = packetWrapper.read(Type.UNSIGNED_BYTE);
                        packetWrapper.write(Type.BOOLEAN, (flags & 1) == 1);
                    }
                });
            }
        });

        //Custom Payload
        this.registerIncoming(State.PLAY, 0x17, 0x17, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        byte[] data = packetWrapper.read(Type.REMAINING_BYTES);
                        packetWrapper.write(Type.SHORT, (short) data.length);
                        for (byte b : data) {
                            packetWrapper.write(Type.BYTE, b);
                        }
                    }
                });
            }
        });

        //Encryption Request
        this.registerOutgoing(State.LOGIN, 0x01, 0x01, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);  //Server ID
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int publicKeyLength = packetWrapper.read(Type.SHORT);
                        packetWrapper.write(Type.VAR_INT, publicKeyLength);
                        packetWrapper.passthrough(new CustomByteType(publicKeyLength));

                        int verifyTokenLength = packetWrapper.read(Type.SHORT);
                        packetWrapper.write(Type.VAR_INT, verifyTokenLength);
                        packetWrapper.passthrough(new CustomByteType(verifyTokenLength));
                    }
                });
            }
        });

        //Encryption Response
        this.registerIncoming(State.LOGIN, 0x01, 0x01, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper packetWrapper) throws Exception {
                        int sharedSecretLength = packetWrapper.read(Type.VAR_INT);
                        packetWrapper.write(Type.SHORT, (short) sharedSecretLength);
                        packetWrapper.passthrough(new CustomByteType(sharedSecretLength));

                        int verifyTokenLength = packetWrapper.read(Type.VAR_INT);
                        packetWrapper.write(Type.SHORT, (short) verifyTokenLength);
                        packetWrapper.passthrough(new CustomByteType(verifyTokenLength));
                    }
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new Tablist(userConnection));
        userConnection.put(new Windows(userConnection));
        userConnection.put(new Scoreboard(userConnection));
        userConnection.put(new EntityTracker(userConnection));
        userConnection.put(new MapStorage(userConnection));
    }

    private boolean isPlayerInsideBlock(long x, long y, long z, byte direction) {
        //switch (direction) {
        //    case 0: {
        //        y--;
        //        break;
        //    }
        //    case 1: {
        //        y++;
        //        break;
        //    }
        //    case 2: {
        //        z--;
        //        break;
        //    }
        //    case 3: {
        //        z++;
        //        break;
        //    }
        //    case 4: {
        //        x--;
        //        break;
        //    }
        //    case 5: {
        //        x++;
        //        break;
        //    }
        //}
        //return Math.abs(The5zigAPI.getAPI().getPlayerPosX() - (x + 0.5)) < 0.8 && Math.abs(The5zigAPI.getAPI().getPlayerPosZ() - (z + 0.5)) < 0.8 && Math.abs((The5zigAPI.getAPI().getPlayerPosY() + 0.9) - (y + 0.5)) < 1.4;
        return false;
    }

    private static ArrayList<Integer> placeable = new ArrayList<>();

    static {
        placeable.add(6);
        placeable.add(27);
        placeable.add(28);
        placeable.add(30);
        placeable.add(31);
        placeable.add(32);
        placeable.add(37);
        placeable.add(38);
        placeable.add(39);
        placeable.add(40);
        placeable.add(50);
        placeable.add(65);
        placeable.add(66);
        placeable.add(69);
        placeable.add(70);
        placeable.add(72);
        placeable.add(76);
        placeable.add(77);
        placeable.add(96);
        placeable.add(106);
        placeable.add(111);
        placeable.add(131);
        placeable.add(143);
        placeable.add(147);
        placeable.add(148);
        placeable.add(157);
        placeable.add(167);
        placeable.add(175);
        for (int i = 256; i <= 378; i++) placeable.add(i);
        for (int i = 381; i <= 396; i++) placeable.add(i);
        for (int i = 398; i <= 452; i++) placeable.add(i);
        for (int i = 2256; i <= 2267; i++) placeable.add(i);
    }

    private boolean isPlaceable(int id) {
        return placeable.contains(id);
    }

    private String getInventoryString(int b) {
        switch (b) {
            case 0:
                return "minecraft:chest";
            case 1:
                return "minecraft:crafting_table";
            case 2:
                return "minecraft:furnace";
            case 3:
                return "minecraft:dispenser";
            case 4:
                return "minecraft:enchanting_table";
            case 5:
                return "minecraft:brewing_stand";
            case 6:
                return "minecraft:villager";
            case 7:
                return "minecraft:beacon";
            case 8:
                return "minecraft:anvil";
            case 9:
                return "minecraft:hopper";
            case 10:
                return "minecraft:dropper";
            case 11:
                return "EntityHorse";
            default:
                throw new IllegalArgumentException("Unknown type " + b);
        }
    }

    private enum Particle {
        EXPLOSION_NORMAL("explode"),
        EXPLOSION_LARGE("largeexplode"),
        EXPLOSION_HUGE("hugeexplosion"),
        FIREWORKS_SPARK("fireworksSpark"),
        WATER_BUBBLE("bubble"),
        WATER_SPLASH("splash"),
        WATER_WAKE("wake"),
        SUSPENDED("suspended"),
        SUSPENDED_DEPTH("depthsuspend"),
        CRIT("crit"),
        CRIT_MAGIC("magicCrit"),
        SMOKE_NORMAL("smoke"),
        SMOKE_LARGE("largesmoke"),
        SPELL("spell"),
        SPELL_INSTANT("instantSpell"),
        SPELL_MOB("mobSpell"),
        SPELL_MOB_AMBIENT("mobSpellAmbient"),
        SPELL_WITCH("witchMagic"),
        DRIP_WATER("dripWater"),
        DRIP_LAVA("dripLava"),
        VILLAGER_ANGRY("angryVillager"),
        VILLAGER_HAPPY("happyVillager"),
        TOWN_AURA("townaura"),
        NOTE("note"),
        PORTAL("portal"),
        ENCHANTMENT_TABLE("enchantmenttable"),
        FLAME("flame"),
        LAVA("lava"),
        FOOTSTEP("footstep"),
        CLOUD("cloud"),
        REDSTONE("reddust"),
        SNOWBALL("snowballpoof"),
        SNOW_SHOVEL("snowshovel"),
        SLIME("slime"),
        HEART("heart"),
        BARRIER("barrier"),
        ICON_CRACK("iconcrack", 2),
        BLOCK_CRACK("blockcrack", 1),
        BLOCK_DUST("blockdust", 1),
        WATER_DROP("droplet"),
        ITEM_TAKE("take"),
        MOB_APPEARANCE("mobappearance");

        public final String name;
        public final int extra;
        private static final HashMap<String, Particle> particleMap = new HashMap<>();

        Particle(String name) {
            this(name, 0);
        }

        Particle(String name, int extra) {
            this.name = name;
            this.extra = extra;
        }

        public static Particle find(String part) {
            return particleMap.get(part);
        }

        static {
            Particle[] particles = values();
            int var1 = particles.length;

            for (Particle particle : particles) {
                particleMap.put(particle.name, particle);
            }

        }
    }
}
