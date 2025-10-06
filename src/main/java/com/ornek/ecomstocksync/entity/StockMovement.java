package com.ornek.ecomstocksync.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialCard material;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;
    
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal unitCost;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal totalCost;
    
    @Column(length = 100)
    private String reference;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 100)
    private String operator;
    
    @Column
    private LocalDateTime movementDate = LocalDateTime.now();
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum MovementType {
        INBOUND,      // Giriş
        OUTBOUND,     // Çıkış
        TRANSFER,     // Transfer
        ADJUSTMENT,   // Düzeltme
        PRODUCTION,   // Üretim
        CONSUMPTION   // Tüketim
    }
    
    // Constructors
    public StockMovement() {}
    
    public StockMovement(MaterialCard material, MovementType movementType, BigDecimal quantity) {
        this.material = material;
        this.movementType = movementType;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    
    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Business methods
    public boolean isInbound() {
        return movementType == MovementType.INBOUND || movementType == MovementType.PRODUCTION;
    }
    
    public boolean isOutbound() {
        return movementType == MovementType.OUTBOUND || movementType == MovementType.CONSUMPTION;
    }
    
    public BigDecimal getEffectiveQuantity() {
        return isInbound() ? quantity : quantity.negate();
    }
    
    @PrePersist
    public void prePersist() {
        if (totalCost == null && unitCost != null && quantity != null) {
            totalCost = unitCost.multiply(quantity);
        }
    }
}
