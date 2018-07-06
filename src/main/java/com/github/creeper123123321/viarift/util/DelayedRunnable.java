package com.github.creeper123123321.viarift.util;

public class DelayedRunnable implements Runnable {
    private Runnable runnable;
    private long delay;

    public DelayedRunnable(Runnable runnable, long delay) {
        this.runnable = runnable;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }
        runnable.run();
    }
}
