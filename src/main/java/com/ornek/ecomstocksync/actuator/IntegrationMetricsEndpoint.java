package com.ornek.ecomstocksync.actuator;

import com.ornek.ecomstocksync.metrics.IntegrationMetricsService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "integration-metrics")
public class IntegrationMetricsEndpoint {

    private final IntegrationMetricsService metricsService;

    public IntegrationMetricsEndpoint(IntegrationMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @ReadOperation
    public Map<String, Object> read() {
        return metricsService.snapshot();
    }
}


