package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.StockMovement;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.StockMovement.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockMovementService {
    
    // Basic CRUD operations
    List<StockMovement> findAll();
    
    Optional<StockMovement> findById(Long id);
    
    List<StockMovement> findByMaterial(MaterialCard material);
    
    List<StockMovement> findByMovementType(MovementType movementType);
    
    List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    StockMovement save(StockMovement stockMovement);
    
    void delete(Long id);
    
    // Stock movement operations
    StockMovement createInboundMovement(MaterialCard material, BigDecimal quantity, 
                                      BigDecimal unitCost, String reference, String description, String operator);
    
    StockMovement createOutboundMovement(MaterialCard material, BigDecimal quantity, 
                                       BigDecimal unitCost, String reference, String description, String operator);
    
    StockMovement createAdjustmentMovement(MaterialCard material, BigDecimal quantity, 
                                         BigDecimal unitCost, String reference, String description, String operator);
    
    StockMovement createTransferMovement(MaterialCard material, BigDecimal quantity, 
                                       BigDecimal unitCost, String reference, String description, String operator);
    
    StockMovement createProductionMovement(MaterialCard material, BigDecimal quantity, 
                                         BigDecimal unitCost, String reference, String description, String operator);
    
    StockMovement createConsumptionMovement(MaterialCard material, BigDecimal quantity, 
                                          BigDecimal unitCost, String reference, String description, String operator);
    
    // Stock calculation operations
    BigDecimal calculateCurrentStock(MaterialCard material);
    
    BigDecimal calculateStockValue(MaterialCard material);
    
    List<StockMovement> getStockHistory(MaterialCard material, int limit);
    
    Map<String, BigDecimal> getStockSummary(MaterialCard material);
    
    // Reporting operations
    List<Map<String, Object>> getMovementReport(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Map<String, Object>> getMovementReportByMaterial(Long materialId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Map<String, Object>> getMovementReportByType(MovementType movementType, LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Object> getMovementStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    // Validation operations
    boolean validateMovement(StockMovement stockMovement);
    
    boolean canCreateOutboundMovement(MaterialCard material, BigDecimal quantity);
    
    // Bulk operations
    List<StockMovement> createBulkMovements(List<StockMovement> movements);
    
    void reverseMovement(Long movementId, String reason, String operator);
    
    // Search and filter operations
    List<StockMovement> searchMovements(String keyword);
    
    List<StockMovement> findByReference(String reference);
    
    List<StockMovement> findByOperator(String operator);
    
    // Dashboard operations
    Map<String, Object> getDashboardSummary();
    
    List<Map<String, Object>> getRecentMovements(int limit);
    
    Map<String, BigDecimal> getMovementTotalsByType(LocalDateTime startDate, LocalDateTime endDate);
}
