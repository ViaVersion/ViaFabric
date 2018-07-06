package com.github.creeper123123321.viarift.platform;

import us.myles.ViaVersion.api.platform.TaskId;

public class VRTaskId implements TaskId {
    private Thread object;

    @Override
    public Thread getObject() {
        return object;
    }

    public VRTaskId(Thread object) {
        this.object = object;
    }
}
