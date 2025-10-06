package com.ornek.ecomstocksync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcomStockSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcomStockSyncApplication.class, args);
    }
}
