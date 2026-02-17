package com.kusuma.fanout.throttle;

public class SimpleRateLimiter {

    private final long intervalNanos;
    private long nextAllowedTime;

    public SimpleRateLimiter(int permitsPerSecond) {
        this.intervalNanos = 1_000_000_000L / permitsPerSecond;
        this.nextAllowedTime = System.nanoTime();
    }

    public synchronized void acquire() {
        long now = System.nanoTime();

        if (now < nextAllowedTime) {
            long sleepTime = nextAllowedTime - now;
            try {
                Thread.sleep(sleepTime / 1_000_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        nextAllowedTime = System.nanoTime() + intervalNanos;
    }
}
