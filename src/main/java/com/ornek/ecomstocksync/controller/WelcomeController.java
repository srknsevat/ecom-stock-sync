package com.ornek.ecomstocksync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ecom Stock Sync API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "products", "/api/products",
            "platforms", "/api/platforms",
            "sync", "/api/sync",
            "health", "/actuator/health",
            "h2-console", "/h2-console"
        ));
        return response;
    }
}
