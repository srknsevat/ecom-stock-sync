package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.repository.PlatformProductRepository;
import com.ornek.ecomstocksync.repository.PlatformRepository;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.service.PlatformService;
import com.ornek.ecomstocksync.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import com.ornek.ecomstocksync.integration.PlatformClientFactory;
import org.springframework.stereotype.Service;
import com.ornek.ecomstocksync.service.SyncHistoryService;
import com.ornek.ecomstocksync.entity.SyncHistory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StockSyncServiceImpl implements StockSyncService {
    
    @Autowired
    private PlatformRepository platformRepository;
    
    @Autowired
    private PlatformProductRepository platformProductRepository;
    
    @Autowired
    private MaterialCardRepository materialRepository;
    
    @Autowired
    private PlatformService platformService;

    @Autowired
    private SyncHistoryService syncHistoryService;

    @Autowired
    private PlatformClientFactory platformClientFactory;
    
    @Override
    public int syncAllPlatforms() {
        List<Platform> platforms = platformService.getPlatformsNeedingSync();
        int totalSynced = 0;
        
        for (Platform platform : platforms) {
            try {
                int synced = syncPlatform(platform.getId());
                totalSynced += synced;
            } catch (Exception e) {
                // Log error but continue with other platforms
                System.err.println("Platform sync failed for " + platform.getName() + ": " + e.getMessage());
            }
        }
        
        return totalSynced;
    }
    
    @Override
    public int syncPlatform(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        List<PlatformProduct> platformProducts = platformProductRepository
            .findByPlatformAndIsActiveTrue(platform);
        
        int syncedCount = 0;
        for (PlatformProduct platformProduct : platformProducts) {
            try {
                // Stub implementation - gerçek platform API çağrıları burada yapılacak
                syncPlatformProduct(platformProduct);
                syncedCount++;
            } catch (Exception e) {
                System.err.println("Product sync failed for " + platformProduct.getProduct().getMaterialName() + 
                                 " on " + platform.getName() + ": " + e.getMessage());
            }
        }
        
        return syncedCount;
    }
    
    @Override
    public int syncProduct(Long productId) {
        MaterialCard material = materialRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + productId));
        
        List<PlatformProduct> platformProducts = platformProductRepository
            .findByMaterial(material);
        
        int syncedCount = 0;
        for (PlatformProduct platformProduct : platformProducts) {
            if (platformProduct.isActive()) {
                try {
                    syncPlatformProduct(platformProduct);
                    syncedCount++;
                } catch (Exception e) {
                    System.err.println("Product sync failed for " + material.getMaterialName() + 
                                     " on " + platformProduct.getPlatform().getName() + ": " + e.getMessage());
                }
            }
        }
        
        return syncedCount;
    }
    
    @Override
    public boolean updatePlatformProductStock(Long platformProductId, Integer newStock) {
        // Credential check (API_KEY as default)
        try {
            PlatformProduct ppCredCheck = platformProductRepository.findById(platformProductId)
                .orElseThrow(() -> new IllegalArgumentException("Platform ürün bulunamadı: " + platformProductId));
            String cred = platformService.getCredential(ppCredCheck.getPlatform().getId(), "API_KEY");
            if (cred == null || cred.isBlank()) {
                try { syncHistoryService.record(ppCredCheck, SyncHistory.Action.STOCK_UPDATE, SyncHistory.Status.FAILURE, "Missing credential API_KEY"); } catch (Exception ignore) {}
                return false;
            }
        } catch (Exception e) { /* fallthrough to original flow which will error meaningfully */ }
        PlatformProduct platformProduct = null;
        try {
            platformProduct = platformProductRepository.findById(platformProductId)
                .orElseThrow(() -> new IllegalArgumentException("Platform ürün bulunamadı: " + platformProductId));

            platformProduct.setStock(newStock);
            platformProductRepository.save(platformProduct);

            // Gerçek platform API çağrısı burada yapılacak
            PlatformClientFactory factory = platformClientFactory;
            factory.getClient(platformProduct.getPlatform()).updateStock(platformProduct.getPlatform(), platformProduct, newStock);

            platformService.markPlatformProductSynced(platformProductId);
            syncHistoryService.record(platformProduct, SyncHistory.Action.STOCK_UPDATE, SyncHistory.Status.SUCCESS, "Stock set to " + newStock);
            return true;
        } catch (Exception e) {
            System.err.println("Stock update failed: " + e.getMessage());
            try { if (platformProduct != null) syncHistoryService.record(platformProduct, SyncHistory.Action.STOCK_UPDATE, SyncHistory.Status.FAILURE, e.getMessage()); } catch (Exception ignore) {}
            return false;
        }
    }
    
    @Override
    public boolean updatePlatformProductPrice(Long platformProductId, BigDecimal newPrice) {
        // Credential check (API_KEY as default)
        try {
            PlatformProduct ppCredCheck = platformProductRepository.findById(platformProductId)
                .orElseThrow(() -> new IllegalArgumentException("Platform ürün bulunamadı: " + platformProductId));
            String cred = platformService.getCredential(ppCredCheck.getPlatform().getId(), "API_KEY");
            if (cred == null || cred.isBlank()) {
                try { syncHistoryService.record(ppCredCheck, SyncHistory.Action.PRICE_UPDATE, SyncHistory.Status.FAILURE, "Missing credential API_KEY"); } catch (Exception ignore) {}
                return false;
            }
        } catch (Exception e) { /* fallthrough */ }
        PlatformProduct platformProduct = null;
        try {
            platformProduct = platformProductRepository.findById(platformProductId)
                .orElseThrow(() -> new IllegalArgumentException("Platform ürün bulunamadı: " + platformProductId));

            platformProduct.setPrice(newPrice);
            platformProductRepository.save(platformProduct);

            // Gerçek platform API çağrısı burada yapılacak
            PlatformClientFactory factory = platformClientFactory;
            factory.getClient(platformProduct.getPlatform()).updatePrice(platformProduct.getPlatform(), platformProduct, newPrice);

            platformService.markPlatformProductSynced(platformProductId);
            syncHistoryService.record(platformProduct, SyncHistory.Action.PRICE_UPDATE, SyncHistory.Status.SUCCESS, "Price set to " + newPrice);
            return true;
        } catch (Exception e) {
            System.err.println("Price update failed: " + e.getMessage());
            try { if (platformProduct != null) syncHistoryService.record(platformProduct, SyncHistory.Action.PRICE_UPDATE, SyncHistory.Status.FAILURE, e.getMessage()); } catch (Exception ignore) {}
            return false;
        }
    }
    
    @Override
    public int propagateStockChange(Long productId, Integer stockChange) {
        MaterialCard material = materialRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + productId));
        
        List<PlatformProduct> platformProducts = platformProductRepository
            .findByMaterial(material);
        
        // Dağıtım: platform bazlı distributionRatio toplami
        int totalRatio = platformProducts.stream()
            .filter(PlatformProduct::isActive)
            .map(pp -> pp.getPlatform().getDistributionRatio() == null ? 0 : pp.getPlatform().getDistributionRatio())
            .reduce(0, Integer::sum);
        boolean useEqual = totalRatio == 0;

        int remaining = stockChange;
        int updatedCount = 0;
        for (int i = 0; i < platformProducts.size(); i++) {
            PlatformProduct platformProduct = platformProducts.get(i);
            if (!platformProduct.isActive()) continue;
            try {
                int portion;
                if (useEqual) {
                    long activeCount = platformProducts.stream().filter(PlatformProduct::isActive).count();
                    portion = Math.round((float) stockChange / (float) activeCount);
                } else {
                    int ratio = platformProduct.getPlatform().getDistributionRatio() == null ? 0 : platformProduct.getPlatform().getDistributionRatio();
                    portion = Math.round(stockChange * (ratio / 100f));
                }
                // son aktif elemana kalan farkı ver
                if (i == platformProducts.size() - 1) {
                    portion = remaining;
                }
                remaining -= portion;
                Integer currentStock = platformProduct.getStock();
                Integer newStock = currentStock + portion;
                if (updatePlatformProductStock(platformProduct.getId(), newStock)) {
                    updatedCount++;
                }
            } catch (Exception e) {
                System.err.println("Stock propagation failed for " + material.getMaterialName() + 
                                 " on " + platformProduct.getPlatform().getName() + ": " + e.getMessage());
            }
        }
        
        return updatedCount;
    }
    
    @Override
    public int propagatePriceChange(Long productId, BigDecimal newPrice) {
        MaterialCard material = materialRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + productId));
        
        List<PlatformProduct> platformProducts = platformProductRepository
            .findByMaterial(material);
        
        int updatedCount = 0;
        for (PlatformProduct platformProduct : platformProducts) {
            if (platformProduct.isActive()) {
                try {
                    if (updatePlatformProductPrice(platformProduct.getId(), newPrice)) {
                        updatedCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Price propagation failed for " + material.getMaterialName() + 
                                     " on " + platformProduct.getPlatform().getName() + ": " + e.getMessage());
                }
            }
        }
        
        return updatedCount;
    }
    
    @Override
    public boolean testPlatformConnection(Long platformId) {
        try {
            Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
            
            // Stub implementation - gerçek platform API testi burada yapılacak
            // return testPlatformApi(platform);
            
            // Şimdilik her zaman true döndür
            return true;
        } catch (Exception e) {
            System.err.println("Platform connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getSyncStatus() {
        Map<String, Object> status = new HashMap<>();
        
        List<Platform> platforms = platformService.getActivePlatforms();
        int totalPlatforms = platforms.size();
        int syncedPlatforms = 0;
        
        for (Platform platform : platforms) {
            if (testPlatformConnection(platform.getId())) {
                syncedPlatforms++;
            }
        }
        
        status.put("totalPlatforms", totalPlatforms);
        status.put("syncedPlatforms", syncedPlatforms);
        status.put("lastSyncTime", LocalDateTime.now());
        status.put("status", syncedPlatforms == totalPlatforms ? "HEALTHY" : "DEGRADED");
        
        return status;
    }
    
    @Override
    public int retryFailedSyncs() {
        // Stub implementation - başarısız senkronizasyonları tekrar dene
        return 0;
    }
    
    @Override
    public List<Map<String, Object>> checkStockConsistency() {
        List<Map<String, Object>> inconsistencies = new ArrayList<>();
        
        // Stub implementation - stok tutarlılığını kontrol et
        // Gerçek implementasyonda, ana ürün stoku ile platform stoklarını karşılaştır
        
        return inconsistencies;
    }
    
    private void syncPlatformProduct(PlatformProduct platformProduct) {
        // Stub implementation - gerçek platform senkronizasyonu burada yapılacak
        // 1. Platform API'sinden güncel stok/fiyat bilgilerini al
        // 2. Yerel veritabanını güncelle
        // 3. Senkronizasyon zamanını işaretle
        
        platformProduct.setLastSyncAt(LocalDateTime.now());
        platformProductRepository.save(platformProduct);
    }
}
