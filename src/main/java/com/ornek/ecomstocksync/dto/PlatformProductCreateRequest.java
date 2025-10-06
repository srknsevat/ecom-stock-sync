
package com.ornek.ecomstocksync.dto;

import jakarta.validation.constraints.NotNull;

public class PlatformProductCreateRequest {
    @NotNull
    private Long productId;

    private String platformProductId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getPlatformProductId() { return platformProductId; }
    public void setPlatformProductId(String platformProductId) { this.platformProductId = platformProductId; }
}
