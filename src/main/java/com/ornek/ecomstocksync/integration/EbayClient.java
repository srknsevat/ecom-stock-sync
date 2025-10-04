
package com.ornek.ecomstocksync.integration;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ornek.ecomstocksync.util.RateLimiter;
import com.ornek.ecomstocksync.util.RetryExecutor;

import java.math.BigDecimal;

@Component
public class EbayClient implements PlatformClient {
    private final RateLimiter rateLimiter;
    private final RetryExecutor retryExecutor;

    public EbayClient(RateLimiter rateLimiter, RetryExecutor retryExecutor) {
        this.rateLimiter = rateLimiter;
        this.retryExecutor = retryExecutor;
    }
    private static final Logger log = LoggerFactory.getLogger(EbayClient.class);

    @Override
    public void updateStock(Platform platform, PlatformProduct platformProduct, Integer newStock) {
        retryExecutor.executeWithRetry(() -> {
            if (!rateLimiter.tryAcquire("ebay", 5, 1.0)) throw new RuntimeException("rate-limited");
            log.info("[eBay] Update stock sku={}, pid={}, newStock={}", platformProduct.getPlatformSku(), platformProduct.getPlatformProductId(), newStock);
        }, 3, 200L);
    }

    @Override
    public void updatePrice(Platform platform, PlatformProduct platformProduct, BigDecimal newPrice) {
        retryExecutor.executeWithRetry(() -> {
            if (!rateLimiter.tryAcquire("ebay", 5, 1.0)) throw new RuntimeException("rate-limited");
            log.info("[eBay] Update price sku={}, pid={}, newPrice={}", platformProduct.getPlatformSku(), platformProduct.getPlatformProductId(), newPrice);
        }, 3, 200L);
    }
}
