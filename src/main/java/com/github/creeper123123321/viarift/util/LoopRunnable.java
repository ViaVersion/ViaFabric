package com.github.creeper123123321.viarift.util;

public class LoopRunnable implements Runnable {
    private Runnable runnable;
    private long interval;

    public LoopRunnable(Runnable runnable, long interval) {
        this.runnable = runnable;
        this.interval = interval;
    }

    @Override
    public void run() {
        while (true) {
            runnable.run();
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
