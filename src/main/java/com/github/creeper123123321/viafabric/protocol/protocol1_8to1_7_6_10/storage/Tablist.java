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

package com.github.creeper123123321.viafabric.protocol.protocol1_8to1_7_6_10.storage;

import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tablist extends StoredObject {

    private ArrayList<TabListEntry> tablist = new ArrayList<>();

    public Tablist(UserConnection user) {
        super(user);
    }

    public static boolean shouldUpdateDisplayName(String oldName, String newName) {
        return oldName == null && newName != null || oldName != null && newName == null || oldName != null && !oldName.equals(newName);
    }

    public TabListEntry getTabListEntry(String name) {
        for (TabListEntry entry : tablist) if (name.equals(entry.name)) return entry;
        return null;
    }

    public TabListEntry getTabListEntry(UUID uuid) {
        for (TabListEntry entry : tablist) if (uuid.equals(entry.uuid)) return entry;
        return null;
    }

    public void remove(TabListEntry entry) {
        tablist.remove(entry);
    }

    public void add(TabListEntry entry) {
        tablist.add(entry);
    }

    public static class TabListEntry {
        public String name;
        public String displayName;
        public UUID uuid;
        public int ping;
        public List<Property> properties = new ArrayList<>();

        public TabListEntry(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }
    }

    public static class Property {
        public String name;
        public String value;
        public String signature;

        public Property(String name, String value, String signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }
    }
}
