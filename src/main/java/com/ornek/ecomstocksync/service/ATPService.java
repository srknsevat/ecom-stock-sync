package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.Platform;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ATPService {
    
    // ATP Calculation
    ATPResult calculateATP(MaterialCard material, BigDecimal requestedQuantity);
    
    ATPResult calculateATPWithBOM(MaterialCard material, BigDecimal requestedQuantity);
    
    Map<MaterialCard, ATPResult> calculateATPForMultipleMaterials(Map<MaterialCard, BigDecimal> materialQuantities);
    
    // Stock Constraint Analysis
    List<StockConstraint> findStockConstraints(MaterialCard material, BigDecimal requestedQuantity);
    
    List<StockConstraint> findStockConstraintsWithBOM(MaterialCard material, BigDecimal requestedQuantity);
    
    StockConstraintAnalysis analyzeStockConstraints(List<MaterialCard> materials, Map<MaterialCard, BigDecimal> quantities);
    
    // Platform Stock Management
    Map<Platform, BigDecimal> calculatePlatformStockDistribution(MaterialCard material, BigDecimal totalQuantity);
    
    void updatePlatformStocks(MaterialCard material, Map<Platform, BigDecimal> platformQuantities);
    
    // ATP Reports
    List<ATPReport> generateATPReport();
    
    List<ATPReport> generateATPReportByCategory(String category);
    
    List<ATPReport> generateATPReportByPlatform(Long platformId);
    
    // Stock Recommendations
    List<StockRecommendation> generateStockRecommendations();
    
    List<StockRecommendation> generateStockRecommendationsByCategory(String category);
    
    // Inner classes for complex return types
    class ATPResult {
        private MaterialCard material;
        private BigDecimal requestedQuantity;
        private BigDecimal availableQuantity;
        private BigDecimal atpQuantity;
        private boolean isAvailable;
        private List<StockConstraint> constraints;
        private BigDecimal cost;
        private LocalDateTime calculatedAt;
        
        // Constructors
        public ATPResult() {
            this.calculatedAt = LocalDateTime.now();
        }
        
        public ATPResult(MaterialCard material, BigDecimal requestedQuantity, 
                        BigDecimal availableQuantity, BigDecimal atpQuantity, 
                        boolean isAvailable, List<StockConstraint> constraints, BigDecimal cost) {
            this.material = material;
            this.requestedQuantity = requestedQuantity;
            this.availableQuantity = availableQuantity;
            this.atpQuantity = atpQuantity;
            this.isAvailable = isAvailable;
            this.constraints = constraints;
            this.cost = cost;
            this.calculatedAt = LocalDateTime.now();
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getRequestedQuantity() { return requestedQuantity; }
        public void setRequestedQuantity(BigDecimal requestedQuantity) { this.requestedQuantity = requestedQuantity; }
        
        public BigDecimal getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(BigDecimal availableQuantity) { this.availableQuantity = availableQuantity; }
        
        public BigDecimal getAtpQuantity() { return atpQuantity; }
        public void setAtpQuantity(BigDecimal atpQuantity) { this.atpQuantity = atpQuantity; }
        
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }
        
        public List<StockConstraint> getConstraints() { return constraints; }
        public void setConstraints(List<StockConstraint> constraints) { this.constraints = constraints; }
        
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        
        public LocalDateTime getCalculatedAt() { return calculatedAt; }
        public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
    }
    
    class StockConstraint {
        private MaterialCard material;
        private BigDecimal requiredQuantity;
        private BigDecimal availableQuantity;
        private BigDecimal shortage;
        private String constraintType;
        private String description;
        private int priority;
        
        // Constructors
        public StockConstraint() {}
        
        public StockConstraint(MaterialCard material, BigDecimal requiredQuantity, 
                             BigDecimal availableQuantity, BigDecimal shortage, 
                             String constraintType, String description, int priority) {
            this.material = material;
            this.requiredQuantity = requiredQuantity;
            this.availableQuantity = availableQuantity;
            this.shortage = shortage;
            this.constraintType = constraintType;
            this.description = description;
            this.priority = priority;
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
        
        public String getConstraintType() { return constraintType; }
        public void setConstraintType(String constraintType) { this.constraintType = constraintType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
    
    class StockConstraintAnalysis {
        private List<StockConstraint> constraints;
        private int totalConstraints;
        private int criticalConstraints;
        private int warningConstraints;
        private BigDecimal totalShortage;
        private BigDecimal totalCost;
        private String analysisSummary;
        
        // Constructors
        public StockConstraintAnalysis() {}
        
        public StockConstraintAnalysis(List<StockConstraint> constraints, int totalConstraints, 
                                     int criticalConstraints, int warningConstraints, 
                                     BigDecimal totalShortage, BigDecimal totalCost, String analysisSummary) {
            this.constraints = constraints;
            this.totalConstraints = totalConstraints;
            this.criticalConstraints = criticalConstraints;
            this.warningConstraints = warningConstraints;
            this.totalShortage = totalShortage;
            this.totalCost = totalCost;
            this.analysisSummary = analysisSummary;
        }
        
        // Getters and Setters
        public List<StockConstraint> getConstraints() { return constraints; }
        public void setConstraints(List<StockConstraint> constraints) { this.constraints = constraints; }
        
        public int getTotalConstraints() { return totalConstraints; }
        public void setTotalConstraints(int totalConstraints) { this.totalConstraints = totalConstraints; }
        
        public int getCriticalConstraints() { return criticalConstraints; }
        public void setCriticalConstraints(int criticalConstraints) { this.criticalConstraints = criticalConstraints; }
        
        public int getWarningConstraints() { return warningConstraints; }
        public void setWarningConstraints(int warningConstraints) { this.warningConstraints = warningConstraints; }
        
        public BigDecimal getTotalShortage() { return totalShortage; }
        public void setTotalShortage(BigDecimal totalShortage) { this.totalShortage = totalShortage; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public String getAnalysisSummary() { return analysisSummary; }
        public void setAnalysisSummary(String analysisSummary) { this.analysisSummary = analysisSummary; }
    }
    
    class ATPReport {
        private MaterialCard material;
        private BigDecimal currentStock;
        private BigDecimal minimumStock;
        private BigDecimal maximumStock;
        private BigDecimal atpQuantity;
        private BigDecimal stockValue;
        private String stockStatus;
        private List<Platform> platforms;
        private LocalDateTime lastUpdated;
        
        // Constructors
        public ATPReport() {}
        
        public ATPReport(MaterialCard material, BigDecimal currentStock, BigDecimal minimumStock, 
                        BigDecimal maximumStock, BigDecimal atpQuantity, BigDecimal stockValue, 
                        String stockStatus, List<Platform> platforms, LocalDateTime lastUpdated) {
            this.material = material;
            this.currentStock = currentStock;
            this.minimumStock = minimumStock;
            this.maximumStock = maximumStock;
            this.atpQuantity = atpQuantity;
            this.stockValue = stockValue;
            this.stockStatus = stockStatus;
            this.platforms = platforms;
            this.lastUpdated = lastUpdated;
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getCurrentStock() { return currentStock; }
        public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
        
        public BigDecimal getMinimumStock() { return minimumStock; }
        public void setMinimumStock(BigDecimal minimumStock) { this.minimumStock = minimumStock; }
        
        public BigDecimal getMaximumStock() { return maximumStock; }
        public void setMaximumStock(BigDecimal maximumStock) { this.maximumStock = maximumStock; }
        
        public BigDecimal getAtpQuantity() { return atpQuantity; }
        public void setAtpQuantity(BigDecimal atpQuantity) { this.atpQuantity = atpQuantity; }
        
        public BigDecimal getStockValue() { return stockValue; }
        public void setStockValue(BigDecimal stockValue) { this.stockValue = stockValue; }
        
        public String getStockStatus() { return stockStatus; }
        public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
        
        public List<Platform> getPlatforms() { return platforms; }
        public void setPlatforms(List<Platform> platforms) { this.platforms = platforms; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
    
    class StockRecommendation {
        private MaterialCard material;
        private BigDecimal currentStock;
        private BigDecimal recommendedStock;
        private BigDecimal stockDifference;
        private String recommendationType;
        private String reason;
        private BigDecimal estimatedCost;
        private int priority;
        
        // Constructors
        public StockRecommendation() {}
        
        public StockRecommendation(MaterialCard material, BigDecimal currentStock, 
                                 BigDecimal recommendedStock, BigDecimal stockDifference, 
                                 String recommendationType, String reason, BigDecimal estimatedCost, int priority) {
            this.material = material;
            this.currentStock = currentStock;
            this.recommendedStock = recommendedStock;
            this.stockDifference = stockDifference;
            this.recommendationType = recommendationType;
            this.reason = reason;
            this.estimatedCost = estimatedCost;
            this.priority = priority;
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getCurrentStock() { return currentStock; }
        public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
        
        public BigDecimal getRecommendedStock() { return recommendedStock; }
        public void setRecommendedStock(BigDecimal recommendedStock) { this.recommendedStock = recommendedStock; }
        
        public BigDecimal getStockDifference() { return stockDifference; }
        public void setStockDifference(BigDecimal stockDifference) { this.stockDifference = stockDifference; }
        
        public String getRecommendationType() { return recommendationType; }
        public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public BigDecimal getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
}

