package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.repository.PlatformProductRepository;
import com.ornek.ecomstocksync.repository.PlatformRepository;
import com.ornek.ecomstocksync.service.ATPService;
import com.ornek.ecomstocksync.service.BillOfMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ATPServiceImpl implements ATPService {
    
    @Autowired
    private MaterialCardRepository materialRepository;
    
    @Autowired
    private PlatformRepository platformRepository;
    
    @Autowired
    private PlatformProductRepository platformProductRepository;
    
    @Autowired
    private BillOfMaterialService bomService;
    
    @Override
    public ATPResult calculateATP(MaterialCard material, BigDecimal requestedQuantity) {
        BigDecimal availableQuantity = material.getCurrentStock();
        BigDecimal atpQuantity = availableQuantity.min(requestedQuantity);
        boolean isAvailable = atpQuantity.compareTo(requestedQuantity) >= 0;
        
        List<StockConstraint> constraints = new ArrayList<>();
        if (!isAvailable) {
            StockConstraint constraint = new StockConstraint(
                material, requestedQuantity, availableQuantity, 
                requestedQuantity.subtract(availableQuantity), 
                "INSUFFICIENT_STOCK", "Yetersiz stok", 1
            );
            constraints.add(constraint);
        }
        
        BigDecimal cost = material.getAverageCost().multiply(atpQuantity);
        
        return new ATPResult(material, requestedQuantity, availableQuantity, 
                           atpQuantity, isAvailable, constraints, cost);
    }
    
    @Override
    public ATPResult calculateATPWithBOM(MaterialCard material, BigDecimal requestedQuantity) {
        // BOM patlatma ile gerekli bileşenleri hesapla
        Map<MaterialCard, BigDecimal> bomExplosion = bomService.explodeBom(material, requestedQuantity);
        
        // En kısıtlı bileşeni bul
        BigDecimal minAvailableRatio = BigDecimal.ONE;
        List<StockConstraint> constraints = new ArrayList<>();
        
        for (Map.Entry<MaterialCard, BigDecimal> entry : bomExplosion.entrySet()) {
            MaterialCard component = entry.getKey();
            BigDecimal requiredQuantity = entry.getValue();
            BigDecimal availableQuantity = component.getCurrentStock();
            
            if (availableQuantity.compareTo(requiredQuantity) < 0) {
                BigDecimal ratio = availableQuantity.divide(requiredQuantity, 4, BigDecimal.ROUND_DOWN);
                if (ratio.compareTo(minAvailableRatio) < 0) {
                    minAvailableRatio = ratio;
                }
                
                StockConstraint constraint = new StockConstraint(
                    component, requiredQuantity, availableQuantity,
                    requiredQuantity.subtract(availableQuantity),
                    "BOM_CONSTRAINT", "BOM kısıtlaması: " + component.getMaterialName(), 1
                );
                constraints.add(constraint);
            }
        }
        
        BigDecimal atpQuantity = requestedQuantity.multiply(minAvailableRatio);
        boolean isAvailable = minAvailableRatio.compareTo(BigDecimal.ONE) >= 0;
        
        // Maliyet hesaplama
        BigDecimal cost = bomService.calculateBomCostWithQuantity(material, atpQuantity);
        
        return new ATPResult(material, requestedQuantity, material.getCurrentStock(), 
                           atpQuantity, isAvailable, constraints, cost);
    }
    
    @Override
    public Map<MaterialCard, ATPResult> calculateATPForMultipleMaterials(Map<MaterialCard, BigDecimal> materialQuantities) {
        Map<MaterialCard, ATPResult> results = new HashMap<>();
        
        for (Map.Entry<MaterialCard, BigDecimal> entry : materialQuantities.entrySet()) {
            MaterialCard material = entry.getKey();
            BigDecimal quantity = entry.getValue();
            
            ATPResult result = calculateATPWithBOM(material, quantity);
            results.put(material, result);
        }
        
        return results;
    }
    
    @Override
    public List<StockConstraint> findStockConstraints(MaterialCard material, BigDecimal requestedQuantity) {
        List<StockConstraint> constraints = new ArrayList<>();
        
        // Temel stok kısıtlaması
        if (material.getCurrentStock().compareTo(requestedQuantity) < 0) {
            StockConstraint constraint = new StockConstraint(
                material, requestedQuantity, material.getCurrentStock(),
                requestedQuantity.subtract(material.getCurrentStock()),
                "INSUFFICIENT_STOCK", "Yetersiz stok", 1
            );
            constraints.add(constraint);
        }
        
        // Minimum stok kısıtlaması
        if (material.getCurrentStock().subtract(requestedQuantity).compareTo(material.getMinimumStock()) < 0) {
            StockConstraint constraint = new StockConstraint(
                material, requestedQuantity, material.getCurrentStock(),
                material.getMinimumStock().subtract(material.getCurrentStock().subtract(requestedQuantity)),
                "MINIMUM_STOCK", "Minimum stok seviyesi altına düşer", 2
            );
            constraints.add(constraint);
        }
        
        return constraints;
    }
    
    @Override
    public List<StockConstraint> findStockConstraintsWithBOM(MaterialCard material, BigDecimal requestedQuantity) {
        List<StockConstraint> constraints = new ArrayList<>();
        
        // BOM patlatma ile bileşen kısıtlamalarını bul
        Map<MaterialCard, BigDecimal> bomExplosion = bomService.explodeBom(material, requestedQuantity);
        
        for (Map.Entry<MaterialCard, BigDecimal> entry : bomExplosion.entrySet()) {
            MaterialCard component = entry.getKey();
            BigDecimal requiredQuantity = entry.getValue();
            
            List<StockConstraint> componentConstraints = findStockConstraints(component, requiredQuantity);
            constraints.addAll(componentConstraints);
        }
        
        return constraints;
    }
    
    @Override
    public StockConstraintAnalysis analyzeStockConstraints(List<MaterialCard> materials, Map<MaterialCard, BigDecimal> quantities) {
        List<StockConstraint> allConstraints = new ArrayList<>();
        
        for (MaterialCard material : materials) {
            BigDecimal quantity = quantities.getOrDefault(material, BigDecimal.ZERO);
            List<StockConstraint> constraints = findStockConstraintsWithBOM(material, quantity);
            allConstraints.addAll(constraints);
        }
        
        // Kısıtlamaları önceliğe göre sırala
        allConstraints.sort(Comparator.comparingInt(StockConstraint::getPriority));
        
        int totalConstraints = allConstraints.size();
        int criticalConstraints = (int) allConstraints.stream()
            .filter(c -> c.getPriority() == 1)
            .count();
        int warningConstraints = (int) allConstraints.stream()
            .filter(c -> c.getPriority() == 2)
            .count();
        
        BigDecimal totalShortage = allConstraints.stream()
            .map(StockConstraint::getShortage)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCost = allConstraints.stream()
            .map(c -> c.getMaterial().getAverageCost().multiply(c.getShortage()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String analysisSummary = String.format(
            "Toplam %d kısıtlama tespit edildi. %d kritik, %d uyarı. Toplam eksik: %s, Tahmini maliyet: %s TL",
            totalConstraints, criticalConstraints, warningConstraints, totalShortage, totalCost
        );
        
        return new StockConstraintAnalysis(allConstraints, totalConstraints, criticalConstraints, 
                                         warningConstraints, totalShortage, totalCost, analysisSummary);
    }
    
    @Override
    public Map<Platform, BigDecimal> calculatePlatformStockDistribution(MaterialCard material, BigDecimal totalQuantity) {
        Map<Platform, BigDecimal> distribution = new HashMap<>();
        
        // Platform ürünlerini bul
        List<PlatformProduct> platformProducts = platformProductRepository.findByMaterial(material);
        
        if (platformProducts.isEmpty()) {
            return distribution;
        }
        
        // Platform dağıtım oranlarını hesapla
        BigDecimal totalRatio = platformProducts.stream()
            .map(pp -> pp.getPlatform().getDistributionRatio() != null ? 
                 BigDecimal.valueOf(pp.getPlatform().getDistributionRatio()) : BigDecimal.ONE)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalRatio.compareTo(BigDecimal.ZERO) == 0) {
            // Eşit dağıtım
            BigDecimal equalRatio = BigDecimal.ONE.divide(BigDecimal.valueOf(platformProducts.size()), 4, BigDecimal.ROUND_HALF_UP);
            for (PlatformProduct pp : platformProducts) {
                distribution.put(pp.getPlatform(), totalQuantity.multiply(equalRatio));
            }
        } else {
            // Oranlı dağıtım
            for (PlatformProduct pp : platformProducts) {
                BigDecimal ratio = pp.getPlatform().getDistributionRatio() != null ? 
                    BigDecimal.valueOf(pp.getPlatform().getDistributionRatio()) : BigDecimal.ONE;
                BigDecimal platformQuantity = totalQuantity.multiply(ratio).divide(totalRatio, 4, BigDecimal.ROUND_HALF_UP);
                distribution.put(pp.getPlatform(), platformQuantity);
            }
        }
        
        return distribution;
    }
    
    @Override
    public void updatePlatformStocks(MaterialCard material, Map<Platform, BigDecimal> platformQuantities) {
        for (Map.Entry<Platform, BigDecimal> entry : platformQuantities.entrySet()) {
            Platform platform = entry.getKey();
            BigDecimal quantity = entry.getValue();
            
            // Platform ürününü bul veya oluştur
            Optional<PlatformProduct> platformProductOpt = platformProductRepository.findByMaterial(material).stream()
                .filter(pp -> pp.getPlatform().getId().equals(platform.getId()))
                .findFirst();
            
            if (platformProductOpt.isPresent()) {
                PlatformProduct platformProduct = platformProductOpt.get();
                platformProduct.setStock(quantity.intValue());
                platformProductRepository.save(platformProduct);
            }
        }
    }
    
    @Override
    public List<ATPReport> generateATPReport() {
        List<MaterialCard> materials = materialRepository.findAll();
        return materials.stream()
            .map(this::createATPReport)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ATPReport> generateATPReportByCategory(String category) {
        List<MaterialCard> materials = materialRepository.findByCategory(category);
        return materials.stream()
            .map(this::createATPReport)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ATPReport> generateATPReportByPlatform(Long platformId) {
        Optional<Platform> platform = platformRepository.findById(platformId);
        if (platform.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<PlatformProduct> platformProducts = platformProductRepository.findByPlatform(platform.get());
        return platformProducts.stream()
            .map(pp -> createATPReport(pp.getProduct()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<StockRecommendation> generateStockRecommendations() {
        List<MaterialCard> materials = materialRepository.findAll();
        return materials.stream()
            .map(this::createStockRecommendation)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<StockRecommendation> generateStockRecommendationsByCategory(String category) {
        List<MaterialCard> materials = materialRepository.findByCategory(category);
        return materials.stream()
            .map(this::createStockRecommendation)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    // Private helper methods
    private ATPReport createATPReport(MaterialCard material) {
        BigDecimal currentStock = material.getCurrentStock();
        BigDecimal minimumStock = material.getMinimumStock();
        BigDecimal maximumStock = material.getMaximumStock();
        BigDecimal atpQuantity = currentStock.subtract(minimumStock).max(BigDecimal.ZERO);
        BigDecimal stockValue = currentStock.multiply(material.getAverageCost());
        
        String stockStatus;
        if (currentStock.compareTo(minimumStock) < 0) {
            stockStatus = "LOW_STOCK";
        } else if (maximumStock.compareTo(BigDecimal.ZERO) > 0 && currentStock.compareTo(maximumStock) > 0) {
            stockStatus = "OVER_STOCK";
        } else {
            stockStatus = "NORMAL";
        }
        
        // Platform bilgilerini al
        List<PlatformProduct> platformProducts = platformProductRepository.findByMaterial(material);
        List<Platform> platforms = platformProducts.stream()
            .map(PlatformProduct::getPlatform)
            .collect(Collectors.toList());
        
        return new ATPReport(material, currentStock, minimumStock, maximumStock, 
                           atpQuantity, stockValue, stockStatus, platforms, LocalDateTime.now());
    }
    
    private StockRecommendation createStockRecommendation(MaterialCard material) {
        BigDecimal currentStock = material.getCurrentStock();
        BigDecimal minimumStock = material.getMinimumStock();
        BigDecimal maximumStock = material.getMaximumStock();
        
        // Düşük stok önerisi
        if (currentStock.compareTo(minimumStock) < 0) {
            BigDecimal recommendedStock = minimumStock.multiply(BigDecimal.valueOf(1.5)); // %50 güvenlik marjı
            BigDecimal stockDifference = recommendedStock.subtract(currentStock);
            BigDecimal estimatedCost = stockDifference.multiply(material.getAverageCost());
            
            return new StockRecommendation(material, currentStock, recommendedStock, 
                                         stockDifference, "INCREASE_STOCK", 
                                         "Stok minimum seviyenin altında", estimatedCost, 1);
        }
        
        // Aşırı stok önerisi
        if (maximumStock.compareTo(BigDecimal.ZERO) > 0 && currentStock.compareTo(maximumStock) > 0) {
            BigDecimal recommendedStock = maximumStock;
            BigDecimal stockDifference = currentStock.subtract(recommendedStock);
            BigDecimal estimatedCost = stockDifference.multiply(material.getAverageCost());
            
            return new StockRecommendation(material, currentStock, recommendedStock, 
                                         stockDifference, "DECREASE_STOCK", 
                                         "Stok maksimum seviyenin üzerinde", estimatedCost, 2);
        }
        
        return null; // Öneri gerekmiyor
    }
}
