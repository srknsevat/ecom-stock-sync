
package com.ornek.ecomstocksync.util;

import com.ornek.ecomstocksync.metrics.IntegrationMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RetryExecutor {
    private static final Logger log = LoggerFactory.getLogger(RetryExecutor.class);

    private final IntegrationMetricsService metricsService;

    public RetryExecutor(IntegrationMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    public void executeWithRetry(Runnable task, int maxAttempts, long backoffMs) {
        int attempt = 1;
        while (true) {
            try {
                task.run();
                if (attempt > 1) {
                    metricsService.incrementRetriesAttempted();
                }
                return;
            } catch (Exception ex) {
                if (attempt >= maxAttempts) {
                    log.error("Retry failed after {} attempts: {}", attempt, ex.getMessage());
                    metricsService.incrementRetriesFailed();
                    return;
                }
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ignored) {}
                attempt++;
            }
        }
    }
}
