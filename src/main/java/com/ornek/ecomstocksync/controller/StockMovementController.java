package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.StockMovement;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.StockMovement.MovementType;
import com.ornek.ecomstocksync.service.StockMovementService;
import com.ornek.ecomstocksync.service.MaterialCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {
    
    @Autowired
    private StockMovementService stockMovementService;
    
    @Autowired
    private MaterialCardService materialService;
    
    // Basic CRUD operations
    @GetMapping
    public List<StockMovement> getAllMovements() {
        return stockMovementService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getMovementById(@PathVariable Long id) {
        Optional<StockMovement> movement = stockMovementService.findById(id);
        return movement.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<StockMovement> createMovement(@Valid @RequestBody StockMovement movement) {
        if (!stockMovementService.validateMovement(movement)) {
            return ResponseEntity.badRequest().build();
        }
        
        StockMovement savedMovement = stockMovementService.save(movement);
        return ResponseEntity.ok(savedMovement);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {
        try {
            stockMovementService.delete(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Movement type operations
    @PostMapping("/inbound")
    public ResponseEntity<StockMovement> createInboundMovement(
            @RequestParam Long materialId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) BigDecimal unitCost,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String operator) {
        
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            StockMovement movement = stockMovementService.createInboundMovement(
                material, quantity, unitCost, reference, description, operator);
            
            return ResponseEntity.ok(movement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/outbound")
    public ResponseEntity<StockMovement> createOutboundMovement(
            @RequestParam Long materialId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) BigDecimal unitCost,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String operator) {
        
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            StockMovement movement = stockMovementService.createOutboundMovement(
                material, quantity, unitCost, reference, description, operator);
            
            return ResponseEntity.ok(movement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/adjustment")
    public ResponseEntity<StockMovement> createAdjustmentMovement(
            @RequestParam Long materialId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) BigDecimal unitCost,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String operator) {
        
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            StockMovement movement = stockMovementService.createAdjustmentMovement(
                material, quantity, unitCost, reference, description, operator);
            
            return ResponseEntity.ok(movement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/production")
    public ResponseEntity<StockMovement> createProductionMovement(
            @RequestParam Long materialId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) BigDecimal unitCost,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String operator) {
        
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            StockMovement movement = stockMovementService.createProductionMovement(
                material, quantity, unitCost, reference, description, operator);
            
            return ResponseEntity.ok(movement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/consumption")
    public ResponseEntity<StockMovement> createConsumptionMovement(
            @RequestParam Long materialId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) BigDecimal unitCost,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String operator) {
        
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            StockMovement movement = stockMovementService.createConsumptionMovement(
                material, quantity, unitCost, reference, description, operator);
            
            return ResponseEntity.ok(movement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Search and filter operations
    @GetMapping("/material/{materialId}")
    public List<StockMovement> getMovementsByMaterial(@PathVariable Long materialId) {
        MaterialCard material = materialService.findById(materialId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
        
        return stockMovementService.findByMaterial(material);
    }
    
    @GetMapping("/type/{movementType}")
    public List<StockMovement> getMovementsByType(@PathVariable MovementType movementType) {
        return stockMovementService.findByMovementType(movementType);
    }
    
    @GetMapping("/date-range")
    public List<StockMovement> getMovementsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return stockMovementService.findByDateRange(startDate, endDate);
    }
    
    @GetMapping("/search")
    public List<StockMovement> searchMovements(@RequestParam String keyword) {
        return stockMovementService.searchMovements(keyword);
    }
    
    @GetMapping("/reference/{reference}")
    public List<StockMovement> getMovementsByReference(@PathVariable String reference) {
        return stockMovementService.findByReference(reference);
    }
    
    @GetMapping("/operator/{operator}")
    public List<StockMovement> getMovementsByOperator(@PathVariable String operator) {
        return stockMovementService.findByOperator(operator);
    }
    
    // Stock calculation operations
    @GetMapping("/material/{materialId}/current-stock")
    public ResponseEntity<BigDecimal> getCurrentStock(@PathVariable Long materialId) {
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            BigDecimal currentStock = stockMovementService.calculateCurrentStock(material);
            return ResponseEntity.ok(currentStock);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/material/{materialId}/stock-summary")
    public ResponseEntity<Map<String, BigDecimal>> getStockSummary(@PathVariable Long materialId) {
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            Map<String, BigDecimal> summary = stockMovementService.getStockSummary(material);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/material/{materialId}/history")
    public List<StockMovement> getStockHistory(@PathVariable Long materialId, 
                                             @RequestParam(defaultValue = "10") int limit) {
        MaterialCard material = materialService.findById(materialId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
        
        return stockMovementService.getStockHistory(material, limit);
    }
    
    // Reporting operations
    @GetMapping("/reports/movement")
    public List<Map<String, Object>> getMovementReport(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return stockMovementService.getMovementReport(startDate, endDate);
    }
    
    @GetMapping("/reports/material/{materialId}")
    public List<Map<String, Object>> getMovementReportByMaterial(
            @PathVariable Long materialId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return stockMovementService.getMovementReportByMaterial(materialId, startDate, endDate);
    }
    
    @GetMapping("/reports/type/{movementType}")
    public List<Map<String, Object>> getMovementReportByType(
            @PathVariable MovementType movementType,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return stockMovementService.getMovementReportByType(movementType, startDate, endDate);
    }
    
    @GetMapping("/statistics")
    public Map<String, Object> getMovementStatistics(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return stockMovementService.getMovementStatistics(startDate, endDate);
    }
    
    // Validation operations
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateMovement(@RequestBody StockMovement movement) {
        boolean isValid = stockMovementService.validateMovement(movement);
        Map<String, Object> result = Map.of(
            "valid", isValid,
            "materialId", movement.getMaterial() != null ? movement.getMaterial().getId() : null,
            "movementType", movement.getMovementType(),
            "quantity", movement.getQuantity()
        );
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/can-outbound")
    public ResponseEntity<Map<String, Object>> canCreateOutboundMovement(
            @RequestParam Long materialId,
            @RequestParam BigDecimal quantity) {
        try {
            MaterialCard material = materialService.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + materialId));
            
            boolean canCreate = stockMovementService.canCreateOutboundMovement(material, quantity);
            BigDecimal currentStock = stockMovementService.calculateCurrentStock(material);
            
            Map<String, Object> result = Map.of(
                "canCreate", canCreate,
                "currentStock", currentStock,
                "requestedQuantity", quantity,
                "materialCode", material.getMaterialCode()
            );
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Bulk operations
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createBulkMovements(@RequestBody List<StockMovement> movements) {
        List<StockMovement> savedMovements = stockMovementService.createBulkMovements(movements);
        
        Map<String, Object> result = Map.of(
            "savedCount", savedMovements.size(),
            "totalCount", movements.size(),
            "savedMovements", savedMovements
        );
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{id}/reverse")
    public ResponseEntity<Void> reverseMovement(@PathVariable Long id,
                                              @RequestParam String reason,
                                              @RequestParam String operator) {
        try {
            stockMovementService.reverseMovement(id, reason, operator);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Dashboard operations
    @GetMapping("/dashboard/summary")
    public Map<String, Object> getDashboardSummary() {
        return stockMovementService.getDashboardSummary();
    }
    
    @GetMapping("/recent")
    public List<Map<String, Object>> getRecentMovements(@RequestParam(defaultValue = "10") int limit) {
        return stockMovementService.getRecentMovements(limit);
    }
    
    @GetMapping("/totals-by-type")
    public Map<String, BigDecimal> getMovementTotalsByType(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return stockMovementService.getMovementTotalsByType(startDate, endDate);
    }
}
