package com.viaversion.fabric.common.platform;

import com.viaversion.viaversion.api.platform.providers.Provider;

public interface NativeVersionProvider extends Provider {
    int getServerProtocolVersion();
}
