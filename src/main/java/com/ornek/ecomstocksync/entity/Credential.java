package com.ornek.ecomstocksync.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "credentials")
public class Credential {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    @NotNull
    private Platform platform;
    
    @NotBlank
    @Column(name = "credential_type", nullable = false)
    private String credentialType; // API_KEY, API_SECRET, ACCESS_TOKEN, etc.
    
    @NotBlank
    @Column(name = "encrypted_value", nullable = false, columnDefinition = "TEXT")
    private String encryptedValue;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Credential() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Credential(Platform platform, String credentialType, String encryptedValue) {
        this();
        this.platform = platform;
        this.credentialType = credentialType;
        this.encryptedValue = encryptedValue;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    
    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }
    
    public String getEncryptedValue() { return encryptedValue; }
    public void setEncryptedValue(String encryptedValue) { this.encryptedValue = encryptedValue; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
