
package com.ornek.ecomstocksync.integration;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.Platform.PlatformType;
import org.springframework.stereotype.Component;
import com.ornek.ecomstocksync.util.RateLimiter;
import com.ornek.ecomstocksync.util.RetryExecutor;

@Component
public class PlatformClientFactory {
    private final RateLimiter rateLimiter;
    private final RetryExecutor retryExecutor;
    private final ShopifyClient shopifyClient;
    private final EbayClient ebayClient;

    public PlatformClientFactory(ShopifyClient shopifyClient, EbayClient ebayClient, RateLimiter rateLimiter, RetryExecutor retryExecutor) {
        this.shopifyClient = shopifyClient;
        this.rateLimiter = rateLimiter;
        this.retryExecutor = retryExecutor;
        this.ebayClient = ebayClient;
    }

    public PlatformClient getClient(Platform platform) {
        PlatformType type = platform.getType();
        if (type == PlatformType.SHOPIFY) return shopifyClient;
        if (type == PlatformType.EBAY) return ebayClient;
        // Varsayılan: log-only davranışı (Shopify client'ı kullan)
        return shopifyClient;
    }
}
