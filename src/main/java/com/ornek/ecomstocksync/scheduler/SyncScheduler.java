package com.ornek.ecomstocksync.scheduler;

import com.ornek.ecomstocksync.service.StockSyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduler {

    private final StockSyncService stockSyncService;

    public SyncScheduler(StockSyncService stockSyncService) {
        this.stockSyncService = stockSyncService;
    }

    // Her 5 dakikada bir tüm platformları senkronize et
    @Scheduled(fixedDelayString = "${sync.fixedDelay.ms:300000}")
    public void syncAll() {
        try {
            stockSyncService.syncAllPlatforms();
        } catch (Exception ignored) { }
    }
}


