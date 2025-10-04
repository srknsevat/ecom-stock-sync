package com.ornek.ecomstocksync.metrics;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IntegrationMetricsService {

    private final Map<String, AtomicInteger> rateLimitedByKey = new ConcurrentHashMap<>();
    private final AtomicInteger retriesAttempted = new AtomicInteger(0);
    private final AtomicInteger retriesFailed = new AtomicInteger(0);

    public void incrementRateLimited(String key) {
        rateLimitedByKey.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void incrementRetriesAttempted() {
        retriesAttempted.incrementAndGet();
    }

    public void incrementRetriesFailed() {
        retriesFailed.incrementAndGet();
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> data = new ConcurrentHashMap<>();
        Map<String, Integer> rl = new ConcurrentHashMap<>();
        rateLimitedByKey.forEach((k, v) -> rl.put(k, v.get()));
        data.put("rateLimitedByKey", rl);
        data.put("retriesAttempted", retriesAttempted.get());
        data.put("retriesFailed", retriesFailed.get());
        return data;
    }
}


