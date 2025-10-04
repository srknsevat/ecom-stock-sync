package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.BillOfMaterial;
import com.ornek.ecomstocksync.entity.MaterialCard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BillOfMaterialService {
    
    List<BillOfMaterial> findAll();
    
    Optional<BillOfMaterial> findById(Long id);
    
    BillOfMaterial save(BillOfMaterial bom);
    
    void deleteById(Long id);
    
    List<BillOfMaterial> findByParentMaterial(MaterialCard parentMaterial);
    
    List<BillOfMaterial> findByChildMaterial(MaterialCard childMaterial);
    
    List<BillOfMaterial> findActiveBomsByParent(MaterialCard parentMaterial);
    
    List<BillOfMaterial> findActiveBomsByChild(MaterialCard childMaterial);
    
    Optional<BillOfMaterial> findActiveBomByParentAndChild(MaterialCard parent, MaterialCard child);
    
    List<BillOfMaterial> findActiveBomsByParentCategory(String category);
    
    List<BillOfMaterial> findActiveBomsByWorkCenter(String workCenter);
    
    List<BillOfMaterial> findActiveBomsByOperation(String operation);
    
    Long countActiveBomsByParent(MaterialCard parent);
    
    Long countActiveBomsByChild(MaterialCard child);
    
    // BOM Management
    void createBom(MaterialCard parent, MaterialCard child, BigDecimal quantity, 
                   BigDecimal unitCost, String operation, String workCenter);
    
    void updateBom(Long bomId, BigDecimal quantity, BigDecimal unitCost, 
                   String operation, String workCenter);
    
    void activateBom(Long bomId);
    
    void deactivateBom(Long bomId);
    
    void setEffectivePeriod(Long bomId, java.time.LocalDateTime effectiveFrom, 
                           java.time.LocalDateTime effectiveTo);
    
    // BOM Explosion (BOM Patlatma)
    Map<MaterialCard, BigDecimal> explodeBom(MaterialCard parentMaterial, BigDecimal quantity);
    
    Map<MaterialCard, BigDecimal> explodeBomWithScrap(MaterialCard parentMaterial, BigDecimal quantity);
    
    List<BomExplosionResult> getDetailedBomExplosion(MaterialCard parentMaterial, BigDecimal quantity);
    
    // Cost Calculation
    BigDecimal calculateBomCost(MaterialCard parentMaterial);
    
    BigDecimal calculateBomCostWithQuantity(MaterialCard parentMaterial, BigDecimal quantity);
    
    Map<MaterialCard, BigDecimal> calculateComponentCosts(MaterialCard parentMaterial, BigDecimal quantity);
    
    // Time Calculation
    BigDecimal calculateTotalOperationTime(MaterialCard parentMaterial);
    
    BigDecimal calculateTotalOperationTimeWithQuantity(MaterialCard parentMaterial, BigDecimal quantity);
    
    Map<String, BigDecimal> calculateWorkCenterTimes(MaterialCard parentMaterial, BigDecimal quantity);
    
    // Validation
    boolean validateBomStructure(MaterialCard parentMaterial);
    
    List<String> getBomValidationErrors(MaterialCard parentMaterial);
    
    boolean hasCircularDependency(MaterialCard parentMaterial, MaterialCard childMaterial);
    
    // Reports
    List<BomReport> getBomReports();
    
    List<BomReport> getBomReportsByCategory(String category);
    
    List<BomReport> getBomReportsByWorkCenter(String workCenter);
    
    // Inner classes for complex return types
    class BomExplosionResult {
        private MaterialCard material;
        private BigDecimal requiredQuantity;
        private BigDecimal availableQuantity;
        private BigDecimal shortage;
        private BigDecimal cost;
        private int level;
        
        // Constructors, getters, setters
        public BomExplosionResult() {}
        
        public BomExplosionResult(MaterialCard material, BigDecimal requiredQuantity, 
                                BigDecimal availableQuantity, BigDecimal shortage, 
                                BigDecimal cost, int level) {
            this.material = material;
            this.requiredQuantity = requiredQuantity;
            this.availableQuantity = availableQuantity;
            this.shortage = shortage;
            this.cost = cost;
            this.level = level;
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public BigDecimal getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(BigDecimal availableQuantity) { this.availableQuantity = availableQuantity; }
        
        public BigDecimal getShortage() { return shortage; }
        public void setShortage(BigDecimal shortage) { this.shortage = shortage; }
        
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
    }
    
    class BomReport {
        private MaterialCard parentMaterial;
        private int componentCount;
        private BigDecimal totalCost;
        private BigDecimal totalTime;
        private String status;
        private java.time.LocalDateTime lastUpdated;
        
        // Constructors, getters, setters
        public BomReport() {}
        
        public BomReport(MaterialCard parentMaterial, int componentCount, 
                        BigDecimal totalCost, BigDecimal totalTime, 
                        String status, java.time.LocalDateTime lastUpdated) {
            this.parentMaterial = parentMaterial;
            this.componentCount = componentCount;
            this.totalCost = totalCost;
            this.totalTime = totalTime;
            this.status = status;
            this.lastUpdated = lastUpdated;
        }
        
        // Getters and Setters
        public MaterialCard getParentMaterial() { return parentMaterial; }
        public void setParentMaterial(MaterialCard parentMaterial) { this.parentMaterial = parentMaterial; }
        
        public int getComponentCount() { return componentCount; }
        public void setComponentCount(int componentCount) { this.componentCount = componentCount; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public BigDecimal getTotalTime() { return totalTime; }
        public void setTotalTime(BigDecimal totalTime) { this.totalTime = totalTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public java.time.LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(java.time.LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
}
