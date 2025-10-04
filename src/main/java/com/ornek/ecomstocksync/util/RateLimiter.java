
package com.ornek.ecomstocksync.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ornek.ecomstocksync.metrics.IntegrationMetricsService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    private static class Bucket {
        int capacity;
        double refillPerSecond;
        double tokens;
        long lastRefillEpochSec;
        Bucket(int capacity, double refillPerSecond) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.tokens = capacity;
            this.lastRefillEpochSec = Instant.now().getEpochSecond();
        }
        synchronized boolean tryAcquire() {
            long now = Instant.now().getEpochSecond();
            long delta = Math.max(0, now - lastRefillEpochSec);
            if (delta > 0) {
                tokens = Math.min(capacity, tokens + delta * refillPerSecond);
                lastRefillEpochSec = now;
            }
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    private final Map<String, Bucket> keyToBucket = new ConcurrentHashMap<>();
    private final IntegrationMetricsService metricsService;

    public RateLimiter(IntegrationMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    public boolean tryAcquire(String key, int capacity, double refillPerSecond) {
        Bucket b = keyToBucket.computeIfAbsent(key, k -> new Bucket(capacity, refillPerSecond));
        boolean ok = b.tryAcquire();
        if (!ok) {
            log.warn("Rate limit exceeded for key={}", key);
            metricsService.incrementRateLimited(key);
        }
        return ok;
    }
}
