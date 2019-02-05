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

import us.myles.ViaVersion.api.minecraft.chunks.NibbleArray;

public class Chunk1_8to1_7_6_10 {
    public ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
    public byte[] blockBiomeArray = new byte[256];
    private boolean skyLight;
    private int primaryBitMask;
    private boolean groundUp;

    public Chunk1_8to1_7_6_10(byte[] data, int primaryBitMask, int addBitMask, boolean skyLight, boolean groundUp) {
        this.primaryBitMask = primaryBitMask;
        this.skyLight = skyLight;
        this.groundUp = groundUp;
        int dataSize = 0;
        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0) {
                if (this.storageArrays[i] == null) this.storageArrays[i] = new ExtendedBlockStorage(i << 4, skyLight);
                byte[] blockIds = this.storageArrays[i].getBlockLSBArray();
                System.arraycopy(data, dataSize, blockIds, 0, blockIds.length);
                dataSize += blockIds.length;
            } else if (this.storageArrays[i] != null && groundUp) {
                this.storageArrays[i] = null;
            }
        }
        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0 && this.storageArrays[i] != null) {
                NibbleArray nibblearray = this.storageArrays[i].getMetadataArray();
                System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                dataSize += nibblearray.getHandle().length;
            }
        }
        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0 && this.storageArrays[i] != null) {
                NibbleArray nibblearray = this.storageArrays[i].getBlocklightArray();
                System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                dataSize += nibblearray.getHandle().length;
            }
        }
        if (skyLight) {
            for (int i = 0; i < this.storageArrays.length; i++) {
                if ((primaryBitMask & 1 << i) != 0 && this.storageArrays[i] != null) {
                    NibbleArray nibblearray = this.storageArrays[i].getSkylightArray();
                    System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                    dataSize += nibblearray.getHandle().length;
                }
            }
        }
        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((addBitMask & 1 << i) != 0) {
                if (this.storageArrays[i] == null) {
                    dataSize += 2048;
                } else {
                    NibbleArray nibblearray = this.storageArrays[i].getBlockMSBArray();
                    if (nibblearray == null) {
                        nibblearray = this.storageArrays[i].createBlockMSBArray();
                    }
                    System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                    dataSize += nibblearray.getHandle().length;
                }
            } else if (groundUp && this.storageArrays[i] != null && this.storageArrays[i].getBlockMSBArray() != null) {
                this.storageArrays[i].clearMSBArray();
            }
        }
        if (groundUp) {
            System.arraycopy(data, dataSize, this.blockBiomeArray, 0, this.blockBiomeArray.length);
        }
    }

    public byte[] get1_8Data() {
        int finalSize = 0;
        int columns = Integer.bitCount(this.primaryBitMask);
        byte[] buffer = new byte[columns * 10240 + (this.skyLight ? columns * 2048 : 0) + 256];

        for (int i = 0; i < storageArrays.length; ++i) {
            if (storageArrays[i] != null && (this.primaryBitMask & 1 << i) != 0 && (!this.groundUp || storageArrays[i].isEmpty())) {
                byte[] blockIds = storageArrays[i].getBlockLSBArray();
                NibbleArray nibblearray = storageArrays[i].getMetadataArray();

                for (int ind = 0; ind < blockIds.length; ++ind) {
                    int id = blockIds[ind] & 255;
                    int px = ind & 15;
                    int py = ind >> 8 & 15;
                    int pz = ind >> 4 & 15;
                    int data = nibblearray.get(px, py, pz);

                    //data = SpigotDebreakifier.getCorrectedData(id, data);

                    char val = (char) (id << 4 | data);
                    buffer[finalSize++] = (byte) (val & 255);
                    buffer[finalSize++] = (byte) (val >> 8 & 255);
                }
            }
        }

        for (int i = 0; i < storageArrays.length; ++i) {
            if (storageArrays[i] != null && (this.primaryBitMask & 1 << i) != 0 && (!this.groundUp || storageArrays[i].isEmpty())) {
                NibbleArray nibblearray = storageArrays[i].getBlocklightArray();
                System.arraycopy(nibblearray.getHandle(), 0, buffer, finalSize, nibblearray.getHandle().length);
                finalSize += nibblearray.getHandle().length;
            }
        }

        if (this.skyLight) {
            for (int i = 0; i < storageArrays.length; ++i) {
                if (storageArrays[i] != null && (this.primaryBitMask & 1 << i) != 0 && (!this.groundUp || storageArrays[i].isEmpty())) {
                    NibbleArray nibblearray = storageArrays[i].getSkylightArray();
                    System.arraycopy(nibblearray.getHandle(), 0, buffer, finalSize, nibblearray.getHandle().length);
                    finalSize += nibblearray.getHandle().length;
                }
            }
        }

        if (this.groundUp) {
            System.arraycopy(blockBiomeArray, 0, buffer, finalSize, blockBiomeArray.length);
            finalSize += blockBiomeArray.length;
        }

        byte[] finaldata = new byte[finalSize];
        System.arraycopy(buffer, 0, finaldata, 0, finalSize);

        return finaldata;
    }
}