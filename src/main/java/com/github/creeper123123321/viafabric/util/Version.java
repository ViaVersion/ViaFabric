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

package com.github.creeper123123321.viafabric.util;

import com.google.common.base.Joiner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Based on ViaVersion's Version
public class Version implements Comparable<Version> {
    private static final Pattern semVer = Pattern.compile("(?<a>0|[1-9]\\d*)\\.(?<b>0|[1-9]\\d*)(?:\\.(?<c>0|[1-9]\\d*))?(?:-(?<tag>[A-z0-9.-]*))?");
    private final int[] parts = new int[3];
    private String tag;

    public Version(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Version can not be null");
        } else {
            Matcher matcher = semVer.matcher(value);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid version format");
            } else {
                this.parts[0] = Integer.parseInt(matcher.group("a"));
                this.parts[1] = Integer.parseInt(matcher.group("b"));
                this.parts[2] = matcher.group("c") == null ? 0 : Integer.parseInt(matcher.group("c"));
                this.tag = matcher.group("tag") == null ? "" : matcher.group("tag");
            }
        }
    }

    public static int compare(Version verA, Version verB) {
        if (verA == verB) {
            return 0;
        } else if (verA == null) {
            return -1;
        } else if (verB == null) {
            return 1;
        } else {
            int max = Math.max(verA.parts.length, verB.parts.length);

            for(int i = 0; i < max; ++i) {
                int partA = i < verA.parts.length ? verA.parts[i] : 0;
                int partB = i < verB.parts.length ? verB.parts[i] : 0;
                if (partA < partB) {
                    return -1;
                }

                if (partA > partB) {
                    return 1;
                }
            }

            // ViaFabric
            if (verA.tag.isEmpty() && verB.tag.isEmpty()) {
                return 0;
            }
            if (verA.tag.isEmpty()) {
                return 1;
            }
            if (verB.tag.isEmpty()) {
                return -1;
            }
            return verA.tag.compareTo(verB.tag);
        }
    }

    public static boolean equals(Version verA, Version verB) {
        return verA == verB || verA != null && verB != null && compare(verA, verB) == 0;
    }

    public String toString() {
        String[] split = new String[this.parts.length];

        for(int i = 0; i < this.parts.length; ++i) {
            split[i] = String.valueOf(this.parts[i]);
        }

        return Joiner.on(".").join(split) + (this.tag.length() != 0 ? "-" + this.tag : "");
    }

    public int compareTo(Version that) {
        return compare(this, that);
    }

    public boolean equals(Object that) {
        return that instanceof Version && equals(this, (Version)that);
    }

    public String getTag() {
        return this.tag;
    }
}
