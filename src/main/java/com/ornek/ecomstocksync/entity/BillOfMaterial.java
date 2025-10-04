package com.ornek.ecomstocksync.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_of_materials")
public class BillOfMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_material_id", nullable = false)
    private MaterialCard parentMaterial;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_material_id", nullable = false)
    private MaterialCard childMaterial;
    
    @Column(precision = 19, scale = 6, nullable = false)
    private BigDecimal quantity;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal unitCost;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal totalCost;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal scrapPercentage = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal operationTime = BigDecimal.ZERO;
    
    @Column(length = 100)
    private String operation;
    
    @Column(length = 50)
    private String workCenter;
    
    @Column(length = 20)
    private String status = "ACTIVE";
    
    @Column
    private LocalDateTime effectiveFrom = LocalDateTime.now();
    
    @Column
    private LocalDateTime effectiveTo;
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public BillOfMaterial() {}
    
    public BillOfMaterial(MaterialCard parentMaterial, MaterialCard childMaterial, BigDecimal quantity) {
        this.parentMaterial = parentMaterial;
        this.childMaterial = childMaterial;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MaterialCard getParentMaterial() { return parentMaterial; }
    public void setParentMaterial(MaterialCard parentMaterial) { this.parentMaterial = parentMaterial; }
    
    public MaterialCard getChildMaterial() { return childMaterial; }
    public void setChildMaterial(MaterialCard childMaterial) { this.childMaterial = childMaterial; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public BigDecimal getScrapPercentage() { return scrapPercentage; }
    public void setScrapPercentage(BigDecimal scrapPercentage) { this.scrapPercentage = scrapPercentage; }
    
    public BigDecimal getOperationTime() { return operationTime; }
    public void setOperationTime(BigDecimal operationTime) { this.operationTime = operationTime; }
    
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    
    public String getWorkCenter() { return workCenter; }
    public void setWorkCenter(String workCenter) { this.workCenter = workCenter; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public boolean isActive() {
        return "ACTIVE".equals(status) && 
               (effectiveTo == null || effectiveTo.isAfter(LocalDateTime.now()));
    }
    
    public BigDecimal getEffectiveQuantity() {
        if (scrapPercentage.compareTo(BigDecimal.ZERO) > 0) {
            return quantity.multiply(BigDecimal.ONE.add(scrapPercentage.divide(BigDecimal.valueOf(100))));
        }
        return quantity;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    public void prePersist() {
        if (totalCost == null && unitCost != null && quantity != null) {
            totalCost = unitCost.multiply(quantity);
        }
    }
}
