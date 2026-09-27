package com.marketing.leadscore.service;

import com.marketing.leadscore.exception.ApiRateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrganizationRateLimiter {

    private final ConcurrentHashMap<Long, Window> windows = new ConcurrentHashMap<>();
    private final int requestLimit;
    private final long windowMillis;
    private final Clock clock = Clock.systemUTC();

    public OrganizationRateLimiter(
            @Value("${leadpulse.rate-limit.requests:120}") int requestLimit,
            @Value("${leadpulse.rate-limit.window-seconds:60}") long windowSeconds) {
        if (requestLimit < 1 || windowSeconds < 1) {
            throw new IllegalArgumentException("Rate-limit settings must be positive.");
        }
        this.requestLimit = requestLimit;
        this.windowMillis = windowSeconds * 1000;
    }

    public void check(Long organizationId) {
        long now = clock.millis();
        Window current = windows.compute(organizationId, (id, previous) -> {
            if (previous == null || now - previous.startedAt >= windowMillis) {
                return new Window(now, 1);
            }
            synchronized (previous) {
                if (now - previous.startedAt >= windowMillis) {
                    return new Window(now, 1);
                }
                previous.count++;
                return previous;
            }
        });
        if (current.count > requestLimit) {
            long retrySeconds = Math.max(1, (current.startedAt + windowMillis - now + 999) / 1000);
            throw new ApiRateLimitExceededException(retrySeconds);
        }
    }

    private static final class Window {
        private final long startedAt;
        private int count;

        private Window(long startedAt, int count) {
            this.startedAt = startedAt;
            this.count = count;
        }
    }
}
