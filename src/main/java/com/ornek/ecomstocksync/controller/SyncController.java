package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")
public class SyncController {
    
    @Autowired
    private StockSyncService stockSyncService;
    
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> syncAllPlatforms() {
        try {
            int syncedCount = stockSyncService.syncAllPlatforms();
            Map<String, Object> result = Map.of(
                "success", true,
                "syncedPlatforms", syncedCount,
                "message", "Tüm platformlar senkronize edildi"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PostMapping("/platform/{platformId}")
    public ResponseEntity<Map<String, Object>> syncPlatform(@PathVariable Long platformId) {
        try {
            int syncedCount = stockSyncService.syncPlatform(platformId);
            Map<String, Object> result = Map.of(
                "success", true,
                "syncedProducts", syncedCount,
                "message", "Platform senkronize edildi"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PostMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> syncProduct(@PathVariable Long productId) {
        try {
            int syncedCount = stockSyncService.syncProduct(productId);
            Map<String, Object> result = Map.of(
                "success", true,
                "syncedPlatforms", syncedCount,
                "message", "Ürün tüm platformlarda senkronize edildi"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PutMapping("/stock/{platformProductId}")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long platformProductId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer newStock = request.get("stock");
            boolean success = stockSyncService.updatePlatformProductStock(platformProductId, newStock);
            
            Map<String, Object> result = Map.of(
                "success", success,
                "message", success ? "Stok güncellendi" : "Stok güncellenemedi"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PutMapping("/price/{platformProductId}")
    public ResponseEntity<Map<String, Object>> updatePrice(
            @PathVariable Long platformProductId,
            @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal newPrice = request.get("price");
            boolean success = stockSyncService.updatePlatformProductPrice(platformProductId, newPrice);
            
            Map<String, Object> result = Map.of(
                "success", success,
                "message", success ? "Fiyat güncellendi" : "Fiyat güncellenemedi"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PostMapping("/propagate/stock/{productId}")
    public ResponseEntity<Map<String, Object>> propagateStockChange(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer stockChange = request.get("stockChange");
            int updatedPlatforms = stockSyncService.propagateStockChange(productId, stockChange);
            
            Map<String, Object> result = Map.of(
                "success", true,
                "updatedPlatforms", updatedPlatforms,
                "message", "Stok değişikliği tüm platformlara yansıtıldı"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PostMapping("/propagate/price/{productId}")
    public ResponseEntity<Map<String, Object>> propagatePriceChange(
            @PathVariable Long productId,
            @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal newPrice = request.get("price");
            int updatedPlatforms = stockSyncService.propagatePriceChange(productId, newPrice);
            
            Map<String, Object> result = Map.of(
                "success", true,
                "updatedPlatforms", updatedPlatforms,
                "message", "Fiyat değişikliği tüm platformlara yansıtıldı"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @GetMapping("/test/{platformId}")
    public ResponseEntity<Map<String, Object>> testConnection(@PathVariable Long platformId) {
        try {
            boolean isConnected = stockSyncService.testPlatformConnection(platformId);
            Map<String, Object> result = Map.of(
                "success", isConnected,
                "connected", isConnected,
                "message", isConnected ? "Platform bağlantısı başarılı" : "Platform bağlantısı başarısız"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSyncStatus() {
        try {
            Map<String, Object> status = stockSyncService.getSyncStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @PostMapping("/retry")
    public ResponseEntity<Map<String, Object>> retryFailedSyncs() {
        try {
            int retryCount = stockSyncService.retryFailedSyncs();
            Map<String, Object> result = Map.of(
                "success", true,
                "retryCount", retryCount,
                "message", "Başarısız senkronizasyonlar tekrar denendi"
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = Map.of(
                "success", false,
                "error", e.getMessage()
            );
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    @GetMapping("/consistency")
    public ResponseEntity<List<Map<String, Object>>> checkConsistency() {
        try {
            List<Map<String, Object>> inconsistencies = stockSyncService.checkStockConsistency();
            return ResponseEntity.ok(inconsistencies);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
