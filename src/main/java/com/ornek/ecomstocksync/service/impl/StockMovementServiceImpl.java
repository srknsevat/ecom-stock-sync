package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.StockMovement;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.StockMovement.MovementType;
import com.ornek.ecomstocksync.repository.StockMovementRepository;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {
    
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    @Autowired
    private MaterialCardRepository materialRepository;
    
    @Override
    public List<StockMovement> findAll() {
        return stockMovementRepository.findAll();
    }
    
    @Override
    public Optional<StockMovement> findById(Long id) {
        return stockMovementRepository.findById(id);
    }
    
    @Override
    public List<StockMovement> findByMaterial(MaterialCard material) {
        return stockMovementRepository.findByMaterialOrderByMovementDateDesc(material);
    }
    
    @Override
    public List<StockMovement> findByMovementType(MovementType movementType) {
        return stockMovementRepository.findByMovementType(movementType);
    }
    
    @Override
    public List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByMovementDateBetween(startDate, endDate);
    }
    
    @Override
    public StockMovement save(StockMovement stockMovement) {
        if (!validateMovement(stockMovement)) {
            throw new IllegalArgumentException("Geçersiz stok hareketi");
        }
        
        // Update material stock
        updateMaterialStock(stockMovement);
        
        return stockMovementRepository.save(stockMovement);
    }
    
    @Override
    public void delete(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Stok hareketi bulunamadı: " + id));
        
        // Reverse the stock impact
        reverseStockImpact(movement);
        
        stockMovementRepository.deleteById(id);
    }
    
    @Override
    public StockMovement createInboundMovement(MaterialCard material, BigDecimal quantity, 
                                             BigDecimal unitCost, String reference, String description, String operator) {
        StockMovement movement = new StockMovement(material, MovementType.INBOUND, quantity);
        movement.setUnitCost(unitCost);
        movement.setReference(reference);
        movement.setDescription(description);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        return save(movement);
    }
    
    @Override
    public StockMovement createOutboundMovement(MaterialCard material, BigDecimal quantity, 
                                              BigDecimal unitCost, String reference, String description, String operator) {
        if (!canCreateOutboundMovement(material, quantity)) {
            throw new IllegalArgumentException("Yetersiz stok: " + material.getMaterialCode());
        }
        
        StockMovement movement = new StockMovement(material, MovementType.OUTBOUND, quantity);
        movement.setUnitCost(unitCost);
        movement.setReference(reference);
        movement.setDescription(description);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        return save(movement);
    }
    
    @Override
    public StockMovement createAdjustmentMovement(MaterialCard material, BigDecimal quantity, 
                                                BigDecimal unitCost, String reference, String description, String operator) {
        StockMovement movement = new StockMovement(material, MovementType.ADJUSTMENT, quantity);
        movement.setUnitCost(unitCost);
        movement.setReference(reference);
        movement.setDescription(description);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        return save(movement);
    }
    
    @Override
    public StockMovement createTransferMovement(MaterialCard material, BigDecimal quantity, 
                                              BigDecimal unitCost, String reference, String description, String operator) {
        StockMovement movement = new StockMovement(material, MovementType.TRANSFER, quantity);
        movement.setUnitCost(unitCost);
        movement.setReference(reference);
        movement.setDescription(description);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        return save(movement);
    }
    
    @Override
    public StockMovement createProductionMovement(MaterialCard material, BigDecimal quantity, 
                                                BigDecimal unitCost, String reference, String description, String operator) {
        StockMovement movement = new StockMovement(material, MovementType.PRODUCTION, quantity);
        movement.setUnitCost(unitCost);
        movement.setReference(reference);
        movement.setDescription(description);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        return save(movement);
    }
    
    @Override
    public StockMovement createConsumptionMovement(MaterialCard material, BigDecimal quantity, 
                                                 BigDecimal unitCost, String reference, String description, String operator) {
        if (!canCreateOutboundMovement(material, quantity)) {
            throw new IllegalArgumentException("Yetersiz stok: " + material.getMaterialCode());
        }
        
        StockMovement movement = new StockMovement(material, MovementType.CONSUMPTION, quantity);
        movement.setUnitCost(unitCost);
        movement.setReference(reference);
        movement.setDescription(description);
        movement.setOperator(operator);
        movement.setMovementDate(LocalDateTime.now());
        
        return save(movement);
    }
    
    @Override
    public BigDecimal calculateCurrentStock(MaterialCard material) {
        List<StockMovement> movements = findByMaterial(material);
        return movements.stream()
            .map(StockMovement::getEffectiveQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public BigDecimal calculateStockValue(MaterialCard material) {
        List<StockMovement> movements = findByMaterial(material);
        return movements.stream()
            .filter(m -> m.getTotalCost() != null)
            .map(StockMovement::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public List<StockMovement> getStockHistory(MaterialCard material, int limit) {
        return findByMaterial(material).stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, BigDecimal> getStockSummary(MaterialCard material) {
        List<StockMovement> movements = findByMaterial(material);
        
        BigDecimal totalInbound = movements.stream()
            .filter(StockMovement::isInbound)
            .map(StockMovement::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOutbound = movements.stream()
            .filter(StockMovement::isOutbound)
            .map(StockMovement::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal currentStock = totalInbound.subtract(totalOutbound);
        BigDecimal totalValue = calculateStockValue(material);
        
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("currentStock", currentStock);
        summary.put("totalInbound", totalInbound);
        summary.put("totalOutbound", totalOutbound);
        summary.put("totalValue", totalValue);
        
        return summary;
    }
    
    @Override
    public List<Map<String, Object>> getMovementReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = findByDateRange(startDate, endDate);
        
        return movements.stream().map(movement -> {
            Map<String, Object> report = new HashMap<>();
            report.put("id", movement.getId());
            report.put("materialCode", movement.getMaterial().getMaterialCode());
            report.put("materialName", movement.getMaterial().getMaterialName());
            report.put("movementType", movement.getMovementType());
            report.put("quantity", movement.getQuantity());
            report.put("unitCost", movement.getUnitCost());
            report.put("totalCost", movement.getTotalCost());
            report.put("reference", movement.getReference());
            report.put("description", movement.getDescription());
            report.put("operator", movement.getOperator());
            report.put("movementDate", movement.getMovementDate());
            return report;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getMovementReportByMaterial(Long materialId, LocalDateTime startDate, LocalDateTime endDate) {
        MaterialCard material = materialRepository.findById(materialId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
        
        List<StockMovement> movements = findByMaterial(material).stream()
            .filter(m -> m.getMovementDate().isAfter(startDate) && m.getMovementDate().isBefore(endDate))
            .collect(Collectors.toList());
        
        return movements.stream().map(movement -> {
            Map<String, Object> report = new HashMap<>();
            report.put("id", movement.getId());
            report.put("movementType", movement.getMovementType());
            report.put("quantity", movement.getQuantity());
            report.put("unitCost", movement.getUnitCost());
            report.put("totalCost", movement.getTotalCost());
            report.put("reference", movement.getReference());
            report.put("description", movement.getDescription());
            report.put("operator", movement.getOperator());
            report.put("movementDate", movement.getMovementDate());
            return report;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getMovementReportByType(MovementType movementType, LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = findByMovementType(movementType).stream()
            .filter(m -> m.getMovementDate().isAfter(startDate) && m.getMovementDate().isBefore(endDate))
            .collect(Collectors.toList());
        
        return movements.stream().map(movement -> {
            Map<String, Object> report = new HashMap<>();
            report.put("id", movement.getId());
            report.put("materialCode", movement.getMaterial().getMaterialCode());
            report.put("materialName", movement.getMaterial().getMaterialName());
            report.put("quantity", movement.getQuantity());
            report.put("unitCost", movement.getUnitCost());
            report.put("totalCost", movement.getTotalCost());
            report.put("reference", movement.getReference());
            report.put("description", movement.getDescription());
            report.put("operator", movement.getOperator());
            report.put("movementDate", movement.getMovementDate());
            return report;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getMovementStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = findByDateRange(startDate, endDate);
        
        Map<String, Long> movementCounts = movements.stream()
            .collect(Collectors.groupingBy(
                m -> m.getMovementType().name(),
                Collectors.counting()
            ));
        
        Map<String, BigDecimal> movementTotals = movements.stream()
            .collect(Collectors.groupingBy(
                m -> m.getMovementType().name(),
                Collectors.mapping(
                    StockMovement::getQuantity,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ));
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalMovements", movements.size());
        statistics.put("movementCounts", movementCounts);
        statistics.put("movementTotals", movementTotals);
        statistics.put("startDate", startDate);
        statistics.put("endDate", endDate);
        
        return statistics;
    }
    
    @Override
    public boolean validateMovement(StockMovement stockMovement) {
        if (stockMovement == null) return false;
        if (stockMovement.getMaterial() == null) return false;
        if (stockMovement.getQuantity() == null || stockMovement.getQuantity().compareTo(BigDecimal.ZERO) <= 0) return false;
        if (stockMovement.getMovementType() == null) return false;
        
        return true;
    }
    
    @Override
    public boolean canCreateOutboundMovement(MaterialCard material, BigDecimal quantity) {
        BigDecimal currentStock = calculateCurrentStock(material);
        return currentStock.compareTo(quantity) >= 0;
    }
    
    @Override
    public List<StockMovement> createBulkMovements(List<StockMovement> movements) {
        List<StockMovement> savedMovements = new ArrayList<>();
        
        for (StockMovement movement : movements) {
            if (validateMovement(movement)) {
                savedMovements.add(save(movement));
            }
        }
        
        return savedMovements;
    }
    
    @Override
    public void reverseMovement(Long movementId, String reason, String operator) {
        StockMovement originalMovement = stockMovementRepository.findById(movementId)
            .orElseThrow(() -> new IllegalArgumentException("Stok hareketi bulunamadı: " + movementId));
        
        // Create reverse movement
        StockMovement reverseMovement = new StockMovement(
            originalMovement.getMaterial(),
            originalMovement.isInbound() ? MovementType.OUTBOUND : MovementType.INBOUND,
            originalMovement.getQuantity()
        );
        
        reverseMovement.setUnitCost(originalMovement.getUnitCost());
        reverseMovement.setReference("REV-" + originalMovement.getReference());
        reverseMovement.setDescription("İptal: " + originalMovement.getDescription() + " | Sebep: " + reason);
        reverseMovement.setOperator(operator);
        reverseMovement.setMovementDate(LocalDateTime.now());
        
        save(reverseMovement);
    }
    
    @Override
    public List<StockMovement> searchMovements(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        return stockMovementRepository.findByReferenceContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            keyword, keyword);
    }
    
    @Override
    public List<StockMovement> findByReference(String reference) {
        return stockMovementRepository.findByReference(reference);
    }
    
    @Override
    public List<StockMovement> findByOperator(String operator) {
        return stockMovementRepository.findByOperator(operator);
    }
    
    @Override
    public Map<String, Object> getDashboardSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        List<StockMovement> todayMovements = findByDateRange(startOfDay, now);
        List<StockMovement> monthMovements = findByDateRange(startOfMonth, now);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("todayMovements", todayMovements.size());
        summary.put("monthMovements", monthMovements.size());
        summary.put("totalMovements", findAll().size());
        
        // Calculate today's totals
        BigDecimal todayInbound = todayMovements.stream()
            .filter(StockMovement::isInbound)
            .map(StockMovement::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal todayOutbound = todayMovements.stream()
            .filter(StockMovement::isOutbound)
            .map(StockMovement::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.put("todayInbound", todayInbound);
        summary.put("todayOutbound", todayOutbound);
        
        return summary;
    }
    
    @Override
    public List<Map<String, Object>> getRecentMovements(int limit) {
        return findAll().stream()
            .sorted((m1, m2) -> m2.getMovementDate().compareTo(m1.getMovementDate()))
            .limit(limit)
            .map(movement -> {
                Map<String, Object> recent = new HashMap<>();
                recent.put("id", movement.getId());
                recent.put("materialCode", movement.getMaterial().getMaterialCode());
                recent.put("materialName", movement.getMaterial().getMaterialName());
                recent.put("movementType", movement.getMovementType());
                recent.put("quantity", movement.getQuantity());
                recent.put("reference", movement.getReference());
                recent.put("operator", movement.getOperator());
                recent.put("movementDate", movement.getMovementDate());
                return recent;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, BigDecimal> getMovementTotalsByType(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = findByDateRange(startDate, endDate);
        
        return movements.stream()
            .collect(Collectors.groupingBy(
                m -> m.getMovementType().name(),
                Collectors.mapping(
                    StockMovement::getQuantity,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ));
    }
    
    // Helper methods
    private void updateMaterialStock(StockMovement movement) {
        MaterialCard material = movement.getMaterial();
        BigDecimal effectiveQuantity = movement.getEffectiveQuantity();
        
        BigDecimal newStock = material.getCurrentStock().add(effectiveQuantity);
        material.setCurrentStock(newStock);
        
        // Update average cost if unit cost is provided
        if (movement.getUnitCost() != null && movement.isInbound()) {
            updateAverageCost(material, movement);
        }
        
        materialRepository.save(material);
    }
    
    private void reverseStockImpact(StockMovement movement) {
        MaterialCard material = movement.getMaterial();
        BigDecimal effectiveQuantity = movement.getEffectiveQuantity().negate();
        
        BigDecimal newStock = material.getCurrentStock().add(effectiveQuantity);
        material.setCurrentStock(newStock);
        
        materialRepository.save(material);
    }
    
    private void updateAverageCost(MaterialCard material, StockMovement movement) {
        // Simple average cost calculation
        // In a real ERP, this would be more sophisticated (FIFO, LIFO, etc.)
        BigDecimal currentValue = material.getCurrentStock().multiply(material.getAverageCost());
        BigDecimal newValue = movement.getQuantity().multiply(movement.getUnitCost());
        BigDecimal totalValue = currentValue.add(newValue);
        BigDecimal totalQuantity = material.getCurrentStock().add(movement.getQuantity());
        
        if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newAverageCost = totalValue.divide(totalQuantity, 4, BigDecimal.ROUND_HALF_UP);
            material.setAverageCost(newAverageCost);
        }
    }
}
