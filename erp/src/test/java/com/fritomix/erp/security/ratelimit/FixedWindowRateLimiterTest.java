package com.fritomix.erp.security.ratelimit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedWindowRateLimiterTest {

    @Test
    void shouldAllowRequestsUpToMaxAttempts() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(5, 60_000);

        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
    }

    @Test
    void shouldRejectRequestsBeyondMaxAttempts() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 60_000);

        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertTrue(limiter.retryAfterSeconds("ip-1") > 0);
        assertTrue(limiter.retryAfterSeconds("ip-1") > 0);
    }

    @Test
    void shouldTreatDifferentIpIndependently() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(2, 60_000);

        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertEquals(0, limiter.retryAfterSeconds("ip-2"));
        assertTrue(limiter.retryAfterSeconds("ip-1") > 0);
    }

    @Test
    void shouldResetWindowAfterElapsedTime() throws InterruptedException {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 50);

        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
        assertTrue(limiter.retryAfterSeconds("ip-1") > 0);
        Thread.sleep(80);
        assertEquals(0, limiter.retryAfterSeconds("ip-1"));
    }
}