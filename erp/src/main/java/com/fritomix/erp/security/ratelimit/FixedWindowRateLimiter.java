package com.fritomix.erp.security.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter {

    private record Window(long startMillis, int count) {}

    private final int maxAttempts;
    private final long windowMillis;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxAttempts, long windowMillis) {
        this.maxAttempts = maxAttempts;
        this.windowMillis = windowMillis;
    }

    public synchronized long retryAfterSeconds(String key) {
        long now = System.currentTimeMillis();
        if (windows.size() > 10_000) {
            windows.entrySet().removeIf(e -> now - e.getValue().startMillis() > windowMillis);
        }
        Window current = windows.get(key);
        if (current == null || now - current.startMillis() >= windowMillis) {
            windows.put(key, new Window(now, 1));
            return 0;
        }
        if (current.count() < maxAttempts) {
            windows.put(key, new Window(current.startMillis(), current.count() + 1));
            return 0;
        }
        long remainingMillis = windowMillis - (now - current.startMillis());
        return Math.max(1, (remainingMillis + 999) / 1000);
    }
}