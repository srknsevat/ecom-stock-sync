package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.StockMovement;
import com.ornek.ecomstocksync.entity.Supplier;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.repository.StockMovementRepository;
import com.ornek.ecomstocksync.service.MaterialCardService;
import com.ornek.ecomstocksync.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialCardServiceImpl implements MaterialCardService {
    
    @Autowired
    private MaterialCardRepository materialCardRepository;
    
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    @Autowired
    private SupplierService supplierService;
    
    @Override
    public List<MaterialCard> findAll() {
        return materialCardRepository.findAll();
    }
    
    @Override
    public Optional<MaterialCard> findById(Long id) {
        return materialCardRepository.findById(id);
    }
    
    @Override
    public Optional<MaterialCard> findByMaterialCode(String materialCode) {
        return materialCardRepository.findByMaterialCode(materialCode);
    }
    
    @Override
    public MaterialCard save(MaterialCard materialCard) {
        return materialCardRepository.save(materialCard);
    }
    
    @Override
    public void deleteById(Long id) {
        materialCardRepository.deleteById(id);
    }
    
    @Override
    public List<MaterialCard> findByCategory(String category) {
        return materialCardRepository.findByCategory(category);
    }
    
    @Override
    public List<MaterialCard> findByStatus(String status) {
        return materialCardRepository.findByStatus(status);
    }
    
    @Override
    public List<MaterialCard> findBySupplier(Supplier supplier) {
        return materialCardRepository.findBySupplier(supplier);
    }
    
    @Override
    public List<MaterialCard> findLowStockMaterials() {
        return materialCardRepository.findLowStockMaterials();
    }
    
    @Override
    public List<MaterialCard> findOverStockMaterials() {
        return materialCardRepository.findOverStockMaterials();
    }
    
    @Override
    public List<MaterialCard> findByNameOrCodeContaining(String name) {
        return materialCardRepository.findByNameOrCodeContaining(name);
    }
    
    @Override
    public List<MaterialCard> findInStockMaterials() {
        return materialCardRepository.findInStockMaterials();
    }
    
    @Override
    public List<MaterialCard> findOutOfStockMaterials() {
        return materialCardRepository.findOutOfStockMaterials();
    }
    
    @Override
    public BigDecimal getTotalStockValue() {
        BigDecimal totalValue = materialCardRepository.getTotalStockValue();
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }
    
    @Override
    public Long countActiveMaterials() {
        return materialCardRepository.countActiveMaterials();
    }
    
    @Override
    public Long countLowStockMaterials() {
        return materialCardRepository.countLowStockMaterials();
    }
    
    @Override
    public void adjustStock(Long materialId, BigDecimal quantity, String reason, String operator) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        // Create stock movement
        StockMovement movement = new StockMovement();
        movement.setMaterial(material);
        movement.setMovementType(StockMovement.MovementType.ADJUSTMENT);
        movement.setQuantity(quantity);
        movement.setDescription(reason);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        stockMovementRepository.save(movement);
        
        // Update material stock
        material.setCurrentStock(material.getCurrentStock().add(quantity));
        materialCardRepository.save(material);
    }
    
    @Override
    public void addStock(Long materialId, BigDecimal quantity, BigDecimal unitCost, String reason, String operator) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        // Create stock movement
        StockMovement movement = new StockMovement();
        movement.setMaterial(material);
        movement.setMovementType(StockMovement.MovementType.INBOUND);
        movement.setQuantity(quantity);
        movement.setUnitCost(unitCost);
        movement.setTotalCost(unitCost.multiply(quantity));
        movement.setDescription(reason);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        stockMovementRepository.save(movement);
        
        // Update material stock and costs
        material.setCurrentStock(material.getCurrentStock().add(quantity));
        
        // Update average cost
        BigDecimal currentValue = material.getCurrentStock().subtract(quantity).multiply(material.getAverageCost());
        BigDecimal newValue = quantity.multiply(unitCost);
        BigDecimal newTotalValue = currentValue.add(newValue);
        BigDecimal newAverageCost = newTotalValue.divide(material.getCurrentStock(), 4, BigDecimal.ROUND_HALF_UP);
        material.setAverageCost(newAverageCost);
        
        // Update last purchase cost
        material.setLastPurchaseCost(unitCost);
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void removeStock(Long materialId, BigDecimal quantity, String reason, String operator) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        if (material.getCurrentStock().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        // Create stock movement
        StockMovement movement = new StockMovement();
        movement.setMaterial(material);
        movement.setMovementType(StockMovement.MovementType.OUTBOUND);
        movement.setQuantity(quantity);
        movement.setUnitCost(material.getAverageCost());
        movement.setTotalCost(material.getAverageCost().multiply(quantity));
        movement.setDescription(reason);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        stockMovementRepository.save(movement);
        
        // Update material stock
        material.setCurrentStock(material.getCurrentStock().subtract(quantity));
        materialCardRepository.save(material);
    }
    
    @Override
    public void transferStock(Long fromMaterialId, Long toMaterialId, BigDecimal quantity, String reason, String operator) {
        // Remove from source
        removeStock(fromMaterialId, quantity, reason + " (Transfer Out)", operator);
        
        // Add to destination
        MaterialCard toMaterial = materialCardRepository.findById(toMaterialId)
            .orElseThrow(() -> new RuntimeException("Destination material not found"));
        
        addStock(toMaterialId, quantity, toMaterial.getAverageCost(), reason + " (Transfer In)", operator);
    }
    
    @Override
    public void updateStandardCost(Long materialId, BigDecimal standardCost) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setStandardCost(standardCost);
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateAverageCost(Long materialId, BigDecimal averageCost) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setAverageCost(averageCost);
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateLastPurchaseCost(Long materialId, BigDecimal lastPurchaseCost) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setLastPurchaseCost(lastPurchaseCost);
        materialCardRepository.save(material);
    }
    
    @Override
    public void assignSupplier(Long materialId, Long supplierId, String supplierCode) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        // Supplier entity'yi bul ve ata
        Supplier supplier = supplierService.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        material.setSupplier(supplier);
        material.setSupplierCode(supplierCode);
        materialCardRepository.save(material);
    }
    
    @Override
    public void removeSupplier(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setSupplier((Supplier) null);
        material.setSupplierCode(null);
        materialCardRepository.save(material);
    }
    
    @Override
    public void setMinimumStock(Long materialId, BigDecimal minimumStock) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setMinimumStock(minimumStock);
        materialCardRepository.save(material);
    }
    
    @Override
    public void setMaximumStock(Long materialId, BigDecimal maximumStock) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setMaximumStock(maximumStock);
        materialCardRepository.save(material);
    }
    
    @Override
    public void activateMaterial(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setStatus("ACTIVE");
        materialCardRepository.save(material);
    }
    
    @Override
    public void deactivateMaterial(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material not found"));
        
        material.setStatus("INACTIVE");
        materialCardRepository.save(material);
    }
    
    @Override
    public List<MaterialCard> getStockReport() {
        return materialCardRepository.findAll();
    }
    
    @Override
    public List<MaterialCard> getCostReport() {
        return materialCardRepository.findAll();
    }
    
    @Override
    public List<MaterialCard> getSupplierReport() {
        return materialCardRepository.findAll();
    }
}
