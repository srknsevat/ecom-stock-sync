package com.ornek.ecomstocksync.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "platforms")
public class Platform {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;
    
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private PlatformType type;
    
    private String baseUrl;
    private String webhookUrl;
    private boolean isActive = true;

    @Column(name = "distribution_ratio")
    private Integer distributionRatio; // 0-100 arası, null ise eşit dağıt
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlatformProduct> platformProducts = new ArrayList<>();
    
    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Credential> credentials = new ArrayList<>();
    
    public Platform() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Platform(String name, String code, PlatformType type) {
        this();
        this.name = name;
        this.code = code;
        this.type = type;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public PlatformType getType() { return type; }
    public void setType(PlatformType type) { this.type = type; }
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Integer getDistributionRatio() { return distributionRatio; }
    public void setDistributionRatio(Integer distributionRatio) { this.distributionRatio = distributionRatio; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<PlatformProduct> getPlatformProducts() { return platformProducts; }
    public void setPlatformProducts(List<PlatformProduct> platformProducts) { this.platformProducts = platformProducts; }
    
    public List<Credential> getCredentials() { return credentials; }
    public void setCredentials(List<Credential> credentials) { this.credentials = credentials; }
    
    public enum PlatformType {
        EBAY, SHOPIFY, AMAZON, WOOCOMMERCE, MAGENTO, CUSTOM
    }
}
