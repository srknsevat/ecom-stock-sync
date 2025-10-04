
package com.ornek.ecomstocksync.integration;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import java.math.BigDecimal;

public interface PlatformClient {
    void updateStock(Platform platform, PlatformProduct platformProduct, Integer newStock);
    void updatePrice(Platform platform, PlatformProduct platformProduct, BigDecimal newPrice);
}
