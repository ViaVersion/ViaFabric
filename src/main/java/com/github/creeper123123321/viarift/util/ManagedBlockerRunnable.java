package com.github.creeper123123321.viarift.util;

import java.util.concurrent.ForkJoinPool;

public class ManagedBlockerRunnable implements ForkJoinPool.ManagedBlocker {
    private final Runnable runnable;
    private boolean release;

    public ManagedBlockerRunnable(Runnable runnable) {
        this.runnable = runnable;
        release = false;
    }

    @Override
    public boolean block() {
        runnable.run();
        release = true;
        return true;
    }

    @Override
    public boolean isReleasable() {
        return release;
    }
}
