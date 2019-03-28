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

package com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10.chunks;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.BlockChangeRecord;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.CustomByteType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ChunkPacketTransformer {
    public static void transformChunk(PacketWrapper packetWrapper) throws Exception {
        int chunkX = packetWrapper.read(Type.INT);
        int chunkZ = packetWrapper.read(Type.INT);
        boolean groundUp = packetWrapper.read(Type.BOOLEAN);
        int primaryBitMask = packetWrapper.read(Type.SHORT);
        int addBitMask = packetWrapper.read(Type.SHORT);
        int compressedSize = packetWrapper.read(Type.INT);
        CustomByteType customByteType = new CustomByteType(compressedSize);
        byte[] data = packetWrapper.read(customByteType);

        int k = 0;
        int l = 0;

        for (int j = 0; j < 16; ++j) {
            k += primaryBitMask >> j & 1;
            l += addBitMask >> j & 1;
        }

        int uncompressedSize = 12288 * k;
        uncompressedSize += 2048 * l;
        if (groundUp) {
            uncompressedSize += 256;
        }

        byte[] uncompressedData = new byte[uncompressedSize];
        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, compressedSize);
        try {
            inflater.inflate(uncompressedData);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        Chunk1_8to1_7_6_10 chunk = new Chunk1_8to1_7_6_10(uncompressedData, primaryBitMask, addBitMask, true, groundUp);

        Field field = PacketWrapper.class.getDeclaredField("packetValues");
        field.setAccessible(true);
        ((List) field.get(packetWrapper)).clear();
        field = PacketWrapper.class.getDeclaredField("readableObjects");
        field.setAccessible(true);
        ((LinkedList) field.get(packetWrapper)).clear();
        field = PacketWrapper.class.getDeclaredField("inputBuffer");
        field.setAccessible(true);
        ByteBuf buffer = (ByteBuf) field.get(packetWrapper);
        buffer.clear();

        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeBoolean(groundUp);
        buffer.writeShort(primaryBitMask);
        byte[] finaldata = chunk.get1_8Data();
        Type.VAR_INT.write(buffer, finaldata.length);
        buffer.writeBytes(finaldata);
    }

    public static void transformChunkBulk(PacketWrapper packetWrapper) throws Exception {
        short columnCount = packetWrapper.read(Type.SHORT);  //short1
        int size = packetWrapper.read(Type.INT);  //size
        boolean skyLightSent = packetWrapper.read(Type.BOOLEAN);  //h
        int[] chunkX = new int[columnCount];  //a
        int[] chunkZ = new int[columnCount];  //b
        int[] primaryBitMask = new int[columnCount];  //c
        int[] addBitMask = new int[columnCount];  //d
        byte[][] inflatedBuffers = new byte[columnCount][];  //inflatedBuffers
        CustomByteType customByteType = new CustomByteType(size);
        byte[] buildBuffer = packetWrapper.read(customByteType);  //buildBuffer

        byte[] data = new byte[196864 * columnCount];  //abyte
        Inflater inflater = new Inflater();
        inflater.setInput(buildBuffer, 0, size);

        try {
            inflater.inflate(data);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int i = 0;

        for (int j = 0; j < columnCount; ++j) {
            chunkX[j] = packetWrapper.read(Type.INT);
            chunkZ[j] = packetWrapper.read(Type.INT);
            primaryBitMask[j] = packetWrapper.read(Type.SHORT);
            addBitMask[j] = packetWrapper.read(Type.SHORT);
            int k = 0;
            int l = 0;

            int i1;
            for (i1 = 0; i1 < 16; ++i1) {
                k += primaryBitMask[j] >> i1 & 1;
                l += addBitMask[j] >> i1 & 1;
            }

            i1 = 8192 * k + 256;
            i1 += 2048 * l;
            if (skyLightSent) {
                i1 += 2048 * k;
            }

            inflatedBuffers[j] = new byte[i1];
            System.arraycopy(data, i, inflatedBuffers[j], 0, i1);
            i += i1;
        }

        Chunk1_8to1_7_6_10[] chunks = new Chunk1_8to1_7_6_10[columnCount];
        for (i = 0; i < columnCount; i++) {
            chunks[i] = new Chunk1_8to1_7_6_10(inflatedBuffers[i], primaryBitMask[i], addBitMask[i], skyLightSent, true);
        }


        packetWrapper.write(Type.BOOLEAN, skyLightSent);
        packetWrapper.write(Type.VAR_INT, (int) columnCount);

        for (i = 0; i < columnCount; ++i) {
            packetWrapper.write(Type.INT, chunkX[i]);
            packetWrapper.write(Type.INT, chunkZ[i]);
            packetWrapper.write(Type.UNSIGNED_SHORT, primaryBitMask[i]);
        }

        for (i = 0; i < columnCount; ++i) {
            data = chunks[i].get1_8Data();
            customByteType = new CustomByteType(data.length);
            packetWrapper.write(customByteType, data);
        }
    }

    public static void transformMultiBlockChange(PacketWrapper packetWrapper) throws Exception {
        int chunkX = packetWrapper.read(Type.INT);
        int chunkZ = packetWrapper.read(Type.INT);
        int size = packetWrapper.read(Type.SHORT);
        packetWrapper.read(Type.INT);
        short[] blocks = new short[size];
        short[] positions = new short[size];

        for (int i = 0; i < size; i++) {
            positions[i] = packetWrapper.read(Type.SHORT);
            blocks[i] = packetWrapper.read(Type.SHORT);
        }

        packetWrapper.write(Type.INT, chunkX);
        packetWrapper.write(Type.INT, chunkZ);
        packetWrapper.write(Type.BLOCK_CHANGE_RECORD_ARRAY, IntStream.range(0, size)
                .mapToObj(it -> new BlockChangeRecord(
                        (short) (positions[it] >>> 8),
                        (short) (positions[it] & 0xFFFF),
                        blocks[it]))
                .toArray(BlockChangeRecord[]::new));
    }
}