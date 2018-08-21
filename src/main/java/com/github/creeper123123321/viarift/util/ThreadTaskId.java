package com.github.creeper123123321.viarift.util;

import us.myles.ViaVersion.api.platform.TaskId;

public class ThreadTaskId implements TaskId {
    private Thread object;

    public ThreadTaskId(Thread object) {
        this.object = object;
    }

    @Override
    public Thread getObject() {
        return object;
    }
}
