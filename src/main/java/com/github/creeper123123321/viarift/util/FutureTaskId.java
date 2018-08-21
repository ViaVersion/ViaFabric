package com.github.creeper123123321.viarift.util;

import us.myles.ViaVersion.api.platform.TaskId;

import java.util.concurrent.Future;

public class FutureTaskId implements TaskId {
    private Future<?> object;

    public FutureTaskId(Future<?> object) {
        this.object = object;
    }

    @Override
    public Future<?> getObject() {
        return object;
    }
}
