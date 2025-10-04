
package com.ornek.ecomstocksync.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_history")
public class SyncHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialCard material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_product_id")
    private PlatformProduct platformProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "detail", length = 1000)
    private String detail;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Action { STOCK_UPDATE, PRICE_UPDATE, FULL_SYNC, PLATFORM_SYNC, PRODUCT_SYNC }
    public enum Status { SUCCESS, FAILURE }

    public Long getId() { return id; }
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    // Backward compatibility
    public MaterialCard getProduct() { return material; }
    public void setProduct(MaterialCard material) { this.material = material; }
    public PlatformProduct getPlatformProduct() { return platformProduct; }
    public void setPlatformProduct(PlatformProduct platformProduct) { this.platformProduct = platformProduct; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
