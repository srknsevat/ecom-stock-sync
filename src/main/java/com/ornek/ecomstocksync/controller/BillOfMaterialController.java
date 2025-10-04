package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.BillOfMaterial;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.service.BillOfMaterialService;
import com.ornek.ecomstocksync.service.MaterialCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/boms")
public class BillOfMaterialController {
    
    @Autowired
    private BillOfMaterialService bomService;
    
    @Autowired
    private MaterialCardService materialService;
    
    @GetMapping
    public List<BillOfMaterial> getAllBoms() {
        return bomService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BillOfMaterial> getBom(@PathVariable Long id) {
        Optional<BillOfMaterial> bom = bomService.findById(id);
        return bom.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public BillOfMaterial createBom(@RequestBody BillOfMaterial bom) {
        return bomService.save(bom);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BillOfMaterial> updateBom(@PathVariable Long id, @RequestBody BillOfMaterial bom) {
        Optional<BillOfMaterial> existingBom = bomService.findById(id);
        if (existingBom.isPresent()) {
            bom.setId(id);
            return ResponseEntity.ok(bomService.save(bom));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBom(@PathVariable Long id) {
        bomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/parent/{parentId}")
    public List<BillOfMaterial> getBomsByParent(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::findByParentMaterial).orElse(List.of());
    }
    
    @GetMapping("/child/{childId}")
    public List<BillOfMaterial> getBomsByChild(@PathVariable Long childId) {
        Optional<MaterialCard> child = materialService.findById(childId);
        return child.map(bomService::findByChildMaterial).orElse(List.of());
    }
    
    @GetMapping("/parent/{parentId}/active")
    public List<BillOfMaterial> getActiveBomsByParent(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::findActiveBomsByParent).orElse(List.of());
    }
    
    @GetMapping("/child/{childId}/active")
    public List<BillOfMaterial> getActiveBomsByChild(@PathVariable Long childId) {
        Optional<MaterialCard> child = materialService.findById(childId);
        return child.map(bomService::findActiveBomsByChild).orElse(List.of());
    }
    
    @GetMapping("/category/{category}")
    public List<BillOfMaterial> getBomsByCategory(@PathVariable String category) {
        return bomService.findActiveBomsByParentCategory(category);
    }
    
    @GetMapping("/work-center/{workCenter}")
    public List<BillOfMaterial> getBomsByWorkCenter(@PathVariable String workCenter) {
        return bomService.findActiveBomsByWorkCenter(workCenter);
    }
    
    @GetMapping("/operation/{operation}")
    public List<BillOfMaterial> getBomsByOperation(@PathVariable String operation) {
        return bomService.findActiveBomsByOperation(operation);
    }
    
    // BOM Management endpoints
    @PostMapping("/create")
    public ResponseEntity<Void> createBom(@RequestParam Long parentId,
                                        @RequestParam Long childId,
                                        @RequestParam BigDecimal quantity,
                                        @RequestParam(required = false) BigDecimal unitCost,
                                        @RequestParam(required = false) String operation,
                                        @RequestParam(required = false) String workCenter) {
        try {
            Optional<MaterialCard> parent = materialService.findById(parentId);
            Optional<MaterialCard> child = materialService.findById(childId);
            
            if (parent.isPresent() && child.isPresent()) {
                bomService.createBom(parent.get(), child.get(), quantity, 
                                   unitCost != null ? unitCost : BigDecimal.ZERO, 
                                   operation, workCenter);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/update")
    public ResponseEntity<Void> updateBom(@PathVariable Long id,
                                        @RequestParam BigDecimal quantity,
                                        @RequestParam(required = false) BigDecimal unitCost,
                                        @RequestParam(required = false) String operation,
                                        @RequestParam(required = false) String workCenter) {
        try {
            bomService.updateBom(id, quantity, 
                               unitCost != null ? unitCost : BigDecimal.ZERO, 
                               operation, workCenter);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateBom(@PathVariable Long id) {
        try {
            bomService.activateBom(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateBom(@PathVariable Long id) {
        try {
            bomService.deactivateBom(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/effective-period")
    public ResponseEntity<Void> setEffectivePeriod(@PathVariable Long id,
                                                 @RequestParam String effectiveFrom,
                                                 @RequestParam(required = false) String effectiveTo) {
        try {
            LocalDateTime from = LocalDateTime.parse(effectiveFrom);
            LocalDateTime to = effectiveTo != null ? LocalDateTime.parse(effectiveTo) : null;
            bomService.setEffectivePeriod(id, from, to);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // BOM Explosion endpoints
    @GetMapping("/{parentId}/explode")
    public Map<MaterialCard, BigDecimal> explodeBom(@PathVariable Long parentId,
                                                   @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.explodeBom(p, quantity)).orElse(Map.of());
    }
    
    @GetMapping("/{parentId}/explode-with-scrap")
    public Map<MaterialCard, BigDecimal> explodeBomWithScrap(@PathVariable Long parentId,
                                                           @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.explodeBomWithScrap(p, quantity)).orElse(Map.of());
    }
    
    @GetMapping("/{parentId}/explode-detailed")
    public List<BillOfMaterialService.BomExplosionResult> getDetailedBomExplosion(@PathVariable Long parentId,
                                                                                 @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.getDetailedBomExplosion(p, quantity)).orElse(List.of());
    }
    
    // Cost Calculation endpoints
    @GetMapping("/{parentId}/cost")
    public BigDecimal calculateBomCost(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::calculateBomCost).orElse(BigDecimal.ZERO);
    }
    
    @GetMapping("/{parentId}/cost-with-quantity")
    public BigDecimal calculateBomCostWithQuantity(@PathVariable Long parentId,
                                                 @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.calculateBomCostWithQuantity(p, quantity)).orElse(BigDecimal.ZERO);
    }
    
    @GetMapping("/{parentId}/component-costs")
    public Map<MaterialCard, BigDecimal> calculateComponentCosts(@PathVariable Long parentId,
                                                               @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.calculateComponentCosts(p, quantity)).orElse(Map.of());
    }
    
    // Time Calculation endpoints
    @GetMapping("/{parentId}/total-time")
    public BigDecimal calculateTotalOperationTime(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::calculateTotalOperationTime).orElse(BigDecimal.ZERO);
    }
    
    @GetMapping("/{parentId}/total-time-with-quantity")
    public BigDecimal calculateTotalOperationTimeWithQuantity(@PathVariable Long parentId,
                                                            @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.calculateTotalOperationTimeWithQuantity(p, quantity)).orElse(BigDecimal.ZERO);
    }
    
    @GetMapping("/{parentId}/work-center-times")
    public Map<String, BigDecimal> calculateWorkCenterTimes(@PathVariable Long parentId,
                                                           @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(p -> bomService.calculateWorkCenterTimes(p, quantity)).orElse(Map.of());
    }
    
    // Validation endpoints
    @GetMapping("/{parentId}/validate")
    public boolean validateBomStructure(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::validateBomStructure).orElse(false);
    }
    
    @GetMapping("/{parentId}/validation-errors")
    public List<String> getBomValidationErrors(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::getBomValidationErrors).orElse(List.of());
    }
    
    @GetMapping("/{parentId}/circular-dependency")
    public boolean hasCircularDependency(@PathVariable Long parentId,
                                       @RequestParam Long childId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        Optional<MaterialCard> child = materialService.findById(childId);
        
        if (parent.isPresent() && child.isPresent()) {
            return bomService.hasCircularDependency(parent.get(), child.get());
        }
        return false;
    }
    
    // Report endpoints
    @GetMapping("/reports")
    public List<BillOfMaterialService.BomReport> getBomReports() {
        return bomService.getBomReports();
    }
    
    @GetMapping("/reports/category/{category}")
    public List<BillOfMaterialService.BomReport> getBomReportsByCategory(@PathVariable String category) {
        return bomService.getBomReportsByCategory(category);
    }
    
    @GetMapping("/reports/work-center/{workCenter}")
    public List<BillOfMaterialService.BomReport> getBomReportsByWorkCenter(@PathVariable String workCenter) {
        return bomService.getBomReportsByWorkCenter(workCenter);
    }
    
    // Statistics endpoints
    @GetMapping("/stats/parent/{parentId}/component-count")
    public Long getComponentCount(@PathVariable Long parentId) {
        Optional<MaterialCard> parent = materialService.findById(parentId);
        return parent.map(bomService::countActiveBomsByParent).orElse(0L);
    }
    
    @GetMapping("/stats/child/{childId}/usage-count")
    public Long getUsageCount(@PathVariable Long childId) {
        Optional<MaterialCard> child = materialService.findById(childId);
        return child.map(bomService::countActiveBomsByChild).orElse(0L);
    }
}
