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

public class ExtendedBlockStorage {
	private int yBase;
	private byte[] blockLSBArray;
	private NibbleArray blockMSBArray;
	private NibbleArray blockMetadataArray;
	private NibbleArray blocklightArray;
	private NibbleArray skylightArray;

	public ExtendedBlockStorage(int paramInt, boolean paramBoolean) {
		this.yBase = paramInt;
		this.blockLSBArray = new byte[4096];
		this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length);
		this.blocklightArray = new NibbleArray(this.blockLSBArray.length);
		if (paramBoolean) {
			this.skylightArray = new NibbleArray(this.blockLSBArray.length);
		}
	}

	public int getExtBlockMetadata(int paramInt1, int paramInt2, int paramInt3) {
		return this.blockMetadataArray.get(paramInt1, paramInt2, paramInt3);
	}

	public void setExtBlockMetadata(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.blockMetadataArray.set(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public int getYLocation() {
		return this.yBase;
	}

	public void setExtSkylightValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.skylightArray.set(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public int getExtSkylightValue(int paramInt1, int paramInt2, int paramInt3) {
		return this.skylightArray.get(paramInt1, paramInt2, paramInt3);
	}

	public void setExtBlocklightValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		this.blocklightArray.set(paramInt1, paramInt2, paramInt3, paramInt4);
	}

	public int getExtBlocklightValue(int paramInt1, int paramInt2, int paramInt3) {
		return this.blocklightArray.get(paramInt1, paramInt2, paramInt3);
	}

	public byte[] getBlockLSBArray() {
		return this.blockLSBArray;
	}

	public boolean isEmpty() {
		return this.blockMSBArray==null;
	}

	public void clearMSBArray() {
		this.blockMSBArray = null;
	}

	public NibbleArray getBlockMSBArray() {
		return this.blockMSBArray;
	}

	public NibbleArray getMetadataArray() {
		return this.blockMetadataArray;
	}

	public NibbleArray getBlocklightArray() {
		return this.blocklightArray;
	}

	public NibbleArray getSkylightArray() {
		return this.skylightArray;
	}

	public void setBlockLSBArray(byte[] paramArrayOfByte) {
		this.blockLSBArray = paramArrayOfByte;
	}

	public void setBlockMSBArray(NibbleArray paramNibbleArray) {
		this.blockMSBArray = paramNibbleArray;
	}

	public void setBlockMetadataArray(NibbleArray paramNibbleArray) {
		this.blockMetadataArray = paramNibbleArray;
	}

	public void setBlocklightArray(NibbleArray paramNibbleArray) {
		this.blocklightArray = paramNibbleArray;
	}

	public void setSkylightArray(NibbleArray paramNibbleArray) {
		this.skylightArray = paramNibbleArray;
	}

	public NibbleArray createBlockMSBArray() {
		this.blockMSBArray = new NibbleArray(this.blockLSBArray.length);
		return this.blockMSBArray;
	}
}
