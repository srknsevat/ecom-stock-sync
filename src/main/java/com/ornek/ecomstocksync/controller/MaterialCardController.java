package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.Supplier;
import com.ornek.ecomstocksync.service.MaterialCardService;
import com.ornek.ecomstocksync.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materials")
public class MaterialCardController {
    
    @Autowired
    private MaterialCardService materialCardService;
    
    @Autowired
    private SupplierService supplierService;
    
    @GetMapping
    public List<MaterialCard> getAllMaterials() {
        return materialCardService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MaterialCard> getMaterial(@PathVariable Long id) {
        Optional<MaterialCard> material = materialCardService.findById(id);
        return material.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{materialCode}")
    public ResponseEntity<MaterialCard> getMaterialByCode(@PathVariable String materialCode) {
        Optional<MaterialCard> material = materialCardService.findByMaterialCode(materialCode);
        return material.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public MaterialCard createMaterial(@RequestBody MaterialCard materialCard) {
        return materialCardService.save(materialCard);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MaterialCard> updateMaterial(@PathVariable Long id, @RequestBody MaterialCard materialCard) {
        Optional<MaterialCard> existingMaterial = materialCardService.findById(id);
        if (existingMaterial.isPresent()) {
            materialCard.setId(id);
            return ResponseEntity.ok(materialCardService.save(materialCard));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/category/{category}")
    public List<MaterialCard> getMaterialsByCategory(@PathVariable String category) {
        return materialCardService.findByCategory(category);
    }
    
    @GetMapping("/status/{status}")
    public List<MaterialCard> getMaterialsByStatus(@PathVariable String status) {
        return materialCardService.findByStatus(status);
    }
    
    @GetMapping("/supplier/{supplierId}")
    public List<MaterialCard> getMaterialsBySupplier(@PathVariable Long supplierId) {
        Supplier supplier = supplierService.findById(supplierId)
            .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        return materialCardService.findBySupplier(supplier);
    }
    
    @GetMapping("/low-stock")
    public List<MaterialCard> getLowStockMaterials() {
        return materialCardService.findLowStockMaterials();
    }
    
    @GetMapping("/over-stock")
    public List<MaterialCard> getOverStockMaterials() {
        return materialCardService.findOverStockMaterials();
    }
    
    @GetMapping("/search")
    public List<MaterialCard> searchMaterials(@RequestParam String name) {
        return materialCardService.findByNameOrCodeContaining(name);
    }
    
    @GetMapping("/in-stock")
    public List<MaterialCard> getInStockMaterials() {
        return materialCardService.findInStockMaterials();
    }
    
    @GetMapping("/out-of-stock")
    public List<MaterialCard> getOutOfStockMaterials() {
        return materialCardService.findOutOfStockMaterials();
    }
    
    @GetMapping("/stats/total-value")
    public BigDecimal getTotalStockValue() {
        return materialCardService.getTotalStockValue();
    }
    
    @GetMapping("/stats/active-count")
    public Long getActiveMaterialsCount() {
        return materialCardService.countActiveMaterials();
    }
    
    @GetMapping("/stats/low-stock-count")
    public Long getLowStockMaterialsCount() {
        return materialCardService.countLowStockMaterials();
    }
    
    // Stock management endpoints
    @PostMapping("/{id}/stock/adjust")
    public ResponseEntity<Void> adjustStock(@PathVariable Long id, 
                                          @RequestParam BigDecimal quantity,
                                          @RequestParam String reason,
                                          @RequestParam String operator) {
        try {
            materialCardService.adjustStock(id, quantity, reason, operator);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/stock/add")
    public ResponseEntity<Void> addStock(@PathVariable Long id,
                                       @RequestParam BigDecimal quantity,
                                       @RequestParam BigDecimal unitCost,
                                       @RequestParam String reason,
                                       @RequestParam String operator) {
        try {
            materialCardService.addStock(id, quantity, unitCost, reason, operator);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/stock/remove")
    public ResponseEntity<Void> removeStock(@PathVariable Long id,
                                          @RequestParam BigDecimal quantity,
                                          @RequestParam String reason,
                                          @RequestParam String operator) {
        try {
            materialCardService.removeStock(id, quantity, reason, operator);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/stock/transfer")
    public ResponseEntity<Void> transferStock(@PathVariable Long id,
                                            @RequestParam Long toMaterialId,
                                            @RequestParam BigDecimal quantity,
                                            @RequestParam String reason,
                                            @RequestParam String operator) {
        try {
            materialCardService.transferStock(id, toMaterialId, quantity, reason, operator);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Cost management endpoints
    @PutMapping("/{id}/cost/standard")
    public ResponseEntity<Void> updateStandardCost(@PathVariable Long id, @RequestParam BigDecimal standardCost) {
        try {
            materialCardService.updateStandardCost(id, standardCost);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/cost/average")
    public ResponseEntity<Void> updateAverageCost(@PathVariable Long id, @RequestParam BigDecimal averageCost) {
        try {
            materialCardService.updateAverageCost(id, averageCost);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/cost/last-purchase")
    public ResponseEntity<Void> updateLastPurchaseCost(@PathVariable Long id, @RequestParam BigDecimal lastPurchaseCost) {
        try {
            materialCardService.updateLastPurchaseCost(id, lastPurchaseCost);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Supplier management endpoints
    @PutMapping("/{id}/supplier")
    public ResponseEntity<Void> assignSupplier(@PathVariable Long id,
                                             @RequestParam Long supplierId,
                                             @RequestParam String supplierCode) {
        try {
            materialCardService.assignSupplier(id, supplierId, supplierCode);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/supplier")
    public ResponseEntity<Void> removeSupplier(@PathVariable Long id) {
        try {
            materialCardService.removeSupplier(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Stock level management endpoints
    @PutMapping("/{id}/minimum-stock")
    public ResponseEntity<Void> setMinimumStock(@PathVariable Long id, @RequestParam BigDecimal minimumStock) {
        try {
            materialCardService.setMinimumStock(id, minimumStock);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/maximum-stock")
    public ResponseEntity<Void> setMaximumStock(@PathVariable Long id, @RequestParam BigDecimal maximumStock) {
        try {
            materialCardService.setMaximumStock(id, maximumStock);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Status management endpoints
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateMaterial(@PathVariable Long id) {
        try {
            materialCardService.activateMaterial(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateMaterial(@PathVariable Long id) {
        try {
            materialCardService.deactivateMaterial(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Report endpoints
    @GetMapping("/reports/stock")
    public List<MaterialCard> getStockReport() {
        return materialCardService.getStockReport();
    }
    
    @GetMapping("/reports/cost")
    public List<MaterialCard> getCostReport() {
        return materialCardService.getCostReport();
    }
    
    @GetMapping("/reports/supplier")
    public List<MaterialCard> getSupplierReport() {
        return materialCardService.getSupplierReport();
    }
}
