package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.StockMovement;
import com.ornek.ecomstocksync.entity.Supplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MaterialCardService {
    
    List<MaterialCard> findAll();
    
    Optional<MaterialCard> findById(Long id);
    
    Optional<MaterialCard> findByMaterialCode(String materialCode);
    
    MaterialCard save(MaterialCard materialCard);
    
    void deleteById(Long id);
    
    List<MaterialCard> findByCategory(String category);
    
    List<MaterialCard> findByStatus(String status);
    
    List<MaterialCard> findBySupplier(Supplier supplier);
    
    List<MaterialCard> findLowStockMaterials();
    
    List<MaterialCard> findOverStockMaterials();
    
    List<MaterialCard> findByNameOrCodeContaining(String name);
    
    List<MaterialCard> findInStockMaterials();
    
    List<MaterialCard> findOutOfStockMaterials();
    
    BigDecimal getTotalStockValue();
    
    Long countActiveMaterials();
    
    Long countLowStockMaterials();
    
    // Stock management
    void adjustStock(Long materialId, BigDecimal quantity, String reason, String operator);
    
    void addStock(Long materialId, BigDecimal quantity, BigDecimal unitCost, String reason, String operator);
    
    void removeStock(Long materialId, BigDecimal quantity, String reason, String operator);
    
    void transferStock(Long fromMaterialId, Long toMaterialId, BigDecimal quantity, String reason, String operator);
    
    // Cost management
    void updateStandardCost(Long materialId, BigDecimal standardCost);
    
    void updateAverageCost(Long materialId, BigDecimal averageCost);
    
    void updateLastPurchaseCost(Long materialId, BigDecimal lastPurchaseCost);
    
    // Supplier management
    void assignSupplier(Long materialId, Long supplierId, String supplierCode);
    
    void removeSupplier(Long materialId);
    
    // Stock level management
    void setMinimumStock(Long materialId, BigDecimal minimumStock);
    
    void setMaximumStock(Long materialId, BigDecimal maximumStock);
    
    // Status management
    void activateMaterial(Long materialId);
    
    void deactivateMaterial(Long materialId);
    
    // Reports
    List<MaterialCard> getStockReport();
    
    List<MaterialCard> getCostReport();
    
    List<MaterialCard> getSupplierReport();
}
