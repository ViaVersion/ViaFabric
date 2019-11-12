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

package com.github.creeper123123321.viafabric.config;

import net.minecraft.SharedConstants;
import us.myles.ViaVersion.util.Config;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VRConfig extends Config {
    public static final String ENABLE_CLIENT_SIDE = "enable-client-side";
    public static final String CLIENT_SIDE_VERSION = "client-side-version";

    public VRConfig(File configFile) {
        super(configFile);
        reloadConfig();
    }

    @Override
    public URL getDefaultConfigURL() {
        return getClass().getClassLoader().getResource("assets/viafabric/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> map) {
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }

    public boolean isClientSideEnabled() {
        return getBoolean(ENABLE_CLIENT_SIDE, false);
    }

    public int getClientSideVersion() {
        int nat = SharedConstants.getGameVersion().getProtocolVersion();
        return !isClientSideEnabled() ? nat : getInt(CLIENT_SIDE_VERSION, -1);
    }

    public void setClientSideEnabled(boolean val) {
        set(ENABLE_CLIENT_SIDE, val);
    }

    public void setClientSideVersion(int val) {
        set(CLIENT_SIDE_VERSION, val);
    }
}
