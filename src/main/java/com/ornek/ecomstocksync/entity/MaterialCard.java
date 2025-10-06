package com.ornek.ecomstocksync.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "material_cards")
public class MaterialCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String materialCode;
    
    @Column(nullable = false)
    private String materialName;
    
    @Column(length = 1000)
    private String description;
    
    @Column(length = 50)
    private String unit;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal currentStock = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal minimumStock = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal maximumStock = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal standardCost = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal averageCost = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4)
    private BigDecimal lastPurchaseCost = BigDecimal.ZERO;
    
    
    @Column(length = 50)
    private String supplierCode;
    
    @Column(length = 100)
    private String storageLocation;
    
    @Column(length = 50)
    private String category;
    
    @Column(length = 20)
    private String status = "ACTIVE";
    
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockMovement> stockMovements = new ArrayList<>();
    
    @OneToMany(mappedBy = "parentMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillOfMaterial> parentBoms = new ArrayList<>();
    
    @OneToMany(mappedBy = "childMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillOfMaterial> childBoms = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    // Constructors
    public MaterialCard() {}
    
    public MaterialCard(String materialCode, String materialName, String unit) {
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.unit = unit;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
    
    public BigDecimal getMinimumStock() { return minimumStock; }
    public void setMinimumStock(BigDecimal minimumStock) { this.minimumStock = minimumStock; }
    
    public BigDecimal getMaximumStock() { return maximumStock; }
    public void setMaximumStock(BigDecimal maximumStock) { this.maximumStock = maximumStock; }
    
    public BigDecimal getStandardCost() { return standardCost; }
    public void setStandardCost(BigDecimal standardCost) { this.standardCost = standardCost; }
    
    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    
    public BigDecimal getLastPurchaseCost() { return lastPurchaseCost; }
    public void setLastPurchaseCost(BigDecimal lastPurchaseCost) { this.lastPurchaseCost = lastPurchaseCost; }
    
    public String getSupplierName() { return supplier != null ? supplier.getSupplierName() : null; }
    public void setSupplierName(String supplierName) { 
        if (supplier != null) {
            supplier.setSupplierName(supplierName);
        }
    }
    
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<StockMovement> getStockMovements() { return stockMovements; }
    public void setStockMovements(List<StockMovement> stockMovements) { this.stockMovements = stockMovements; }
    
    public List<BillOfMaterial> getParentBoms() { return parentBoms; }
    public void setParentBoms(List<BillOfMaterial> parentBoms) { this.parentBoms = parentBoms; }
    
    public List<BillOfMaterial> getChildBoms() { return childBoms; }
    public void setChildBoms(List<BillOfMaterial> childBoms) { this.childBoms = childBoms; }
    
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    
    // Business methods
    public boolean isLowStock() {
        return currentStock.compareTo(minimumStock) < 0;
    }
    
    public boolean isOverStock() {
        return maximumStock.compareTo(BigDecimal.ZERO) > 0 && currentStock.compareTo(maximumStock) > 0;
    }
    
    public BigDecimal getStockValue() {
        return currentStock.multiply(averageCost);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
