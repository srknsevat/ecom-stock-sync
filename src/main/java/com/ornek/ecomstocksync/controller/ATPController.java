package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.service.ATPService;
import com.ornek.ecomstocksync.service.MaterialCardService;
import com.ornek.ecomstocksync.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/atp")
public class ATPController {
    
    @Autowired
    private ATPService atpService;
    
    @Autowired
    private MaterialCardService materialService;
    
    @Autowired
    private PlatformService platformService;
    
    // ATP Calculation endpoints
    @GetMapping("/{materialId}")
    public ResponseEntity<ATPService.ATPResult> calculateATP(@PathVariable Long materialId,
                                                           @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        if (material.isPresent()) {
            ATPService.ATPResult result = atpService.calculateATP(material.get(), quantity);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{materialId}/with-bom")
    public ResponseEntity<ATPService.ATPResult> calculateATPWithBOM(@PathVariable Long materialId,
                                                                  @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        if (material.isPresent()) {
            ATPService.ATPResult result = atpService.calculateATPWithBOM(material.get(), quantity);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/multiple")
    public Map<MaterialCard, ATPService.ATPResult> calculateATPForMultipleMaterials(
            @RequestBody Map<Long, BigDecimal> materialQuantities) {
        Map<MaterialCard, BigDecimal> materials = materialQuantities.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                entry -> materialService.findById(entry.getKey()).orElse(null),
                Map.Entry::getValue
            ));
        
        // Null değerleri filtrele
        materials = materials.entrySet().stream()
            .filter(entry -> entry.getKey() != null)
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
        
        return atpService.calculateATPForMultipleMaterials(materials);
    }
    
    // Stock Constraint Analysis endpoints
    @GetMapping("/{materialId}/constraints")
    public List<ATPService.StockConstraint> findStockConstraints(@PathVariable Long materialId,
                                                               @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        return material.map(m -> atpService.findStockConstraints(m, quantity))
                      .orElse(List.of());
    }
    
    @GetMapping("/{materialId}/constraints/with-bom")
    public List<ATPService.StockConstraint> findStockConstraintsWithBOM(@PathVariable Long materialId,
                                                                      @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        return material.map(m -> atpService.findStockConstraintsWithBOM(m, quantity))
                      .orElse(List.of());
    }
    
    @PostMapping("/constraints/analyze")
    public ATPService.StockConstraintAnalysis analyzeStockConstraints(
            @RequestBody Map<Long, BigDecimal> materialQuantities) {
        Map<MaterialCard, BigDecimal> materials = materialQuantities.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                entry -> materialService.findById(entry.getKey()).orElse(null),
                Map.Entry::getValue
            ));
        
        // Null değerleri filtrele
        materials = materials.entrySet().stream()
            .filter(entry -> entry.getKey() != null)
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
        
        List<MaterialCard> materialList = materials.keySet().stream().toList();
        return atpService.analyzeStockConstraints(materialList, materials);
    }
    
    // Platform Stock Management endpoints
    @GetMapping("/{materialId}/platform-distribution")
    public Map<Platform, BigDecimal> calculatePlatformStockDistribution(@PathVariable Long materialId,
                                                                      @RequestParam BigDecimal totalQuantity) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        return material.map(m -> atpService.calculatePlatformStockDistribution(m, totalQuantity))
                      .orElse(Map.of());
    }
    
    @PostMapping("/{materialId}/update-platform-stocks")
    public ResponseEntity<Void> updatePlatformStocks(@PathVariable Long materialId,
                                                   @RequestBody Map<Long, BigDecimal> platformQuantities) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        if (material.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Map<Platform, BigDecimal> platforms = platformQuantities.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                entry -> platformService.getPlatformById(entry.getKey()).orElse(null),
                Map.Entry::getValue
            ));
        
        // Null değerleri filtrele
        platforms = platforms.entrySet().stream()
            .filter(entry -> entry.getKey() != null)
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
        
        atpService.updatePlatformStocks(material.get(), platforms);
        return ResponseEntity.ok().build();
    }
    
    // ATP Reports endpoints
    @GetMapping("/reports")
    public List<ATPService.ATPReport> generateATPReport() {
        return atpService.generateATPReport();
    }
    
    @GetMapping("/reports/category/{category}")
    public List<ATPService.ATPReport> generateATPReportByCategory(@PathVariable String category) {
        return atpService.generateATPReportByCategory(category);
    }
    
    @GetMapping("/reports/platform/{platformId}")
    public List<ATPService.ATPReport> generateATPReportByPlatform(@PathVariable Long platformId) {
        return atpService.generateATPReportByPlatform(platformId);
    }
    
    // Stock Recommendations endpoints
    @GetMapping("/recommendations")
    public List<ATPService.StockRecommendation> generateStockRecommendations() {
        return atpService.generateStockRecommendations();
    }
    
    @GetMapping("/recommendations/category/{category}")
    public List<ATPService.StockRecommendation> generateStockRecommendationsByCategory(@PathVariable String category) {
        return atpService.generateStockRecommendationsByCategory(category);
    }
    
    // Dashboard endpoints
    @GetMapping("/dashboard/summary")
    public Map<String, Object> getATPDashboardSummary() {
        List<ATPService.ATPReport> reports = atpService.generateATPReport();
        List<ATPService.StockRecommendation> recommendations = atpService.generateStockRecommendations();
        
        long totalMaterials = reports.size();
        long lowStockMaterials = reports.stream()
            .filter(r -> "LOW_STOCK".equals(r.getStockStatus()))
            .count();
        long overStockMaterials = reports.stream()
            .filter(r -> "OVER_STOCK".equals(r.getStockStatus()))
            .count();
        long normalStockMaterials = reports.stream()
            .filter(r -> "NORMAL".equals(r.getStockStatus()))
            .count();
        
        BigDecimal totalStockValue = reports.stream()
            .map(ATPService.ATPReport::getStockValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int criticalRecommendations = (int) recommendations.stream()
            .filter(r -> r.getPriority() == 1)
            .count();
        int warningRecommendations = (int) recommendations.stream()
            .filter(r -> r.getPriority() == 2)
            .count();
        
        return Map.of(
            "totalMaterials", totalMaterials,
            "lowStockMaterials", lowStockMaterials,
            "overStockMaterials", overStockMaterials,
            "normalStockMaterials", normalStockMaterials,
            "totalStockValue", totalStockValue,
            "criticalRecommendations", criticalRecommendations,
            "warningRecommendations", warningRecommendations,
            "totalRecommendations", recommendations.size()
        );
    }
    
    // Quick ATP Check endpoint
    @GetMapping("/quick-check")
    public Map<String, Object> quickATPCheck(@RequestParam Long materialId,
                                            @RequestParam BigDecimal quantity) {
        Optional<MaterialCard> material = materialService.findById(materialId);
        if (material.isEmpty()) {
            return Map.of("error", "Material not found");
        }
        
        ATPService.ATPResult result = atpService.calculateATPWithBOM(material.get(), quantity);
        
        return Map.of(
            "materialCode", material.get().getMaterialCode(),
            "materialName", material.get().getMaterialName(),
            "requestedQuantity", quantity,
            "availableQuantity", result.getAvailableQuantity(),
            "atpQuantity", result.getAtpQuantity(),
            "isAvailable", result.isAvailable(),
            "constraintsCount", result.getConstraints().size(),
            "cost", result.getCost()
        );
    }
}
