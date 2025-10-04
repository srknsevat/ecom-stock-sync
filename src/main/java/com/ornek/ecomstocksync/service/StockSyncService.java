package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;

import java.util.List;
import java.util.Map;

public interface StockSyncService {
    
    /**
     * Tüm platformlarda stok senkronizasyonu yapar
     * @return Senkronize edilen platform sayısı
     */
    int syncAllPlatforms();
    
    /**
     * Belirli bir platformda stok senkronizasyonu yapar
     * @param platformId Platform ID
     * @return Senkronize edilen ürün sayısı
     */
    int syncPlatform(Long platformId);
    
    /**
     * Belirli bir ürünün tüm platformlardaki stoklarını senkronize eder
     * @param productId Ürün ID
     * @return Senkronize edilen platform sayısı
     */
    int syncProduct(Long productId);
    
    /**
     * Platform ürün stokunu günceller
     * @param platformProductId Platform ürün ID
     * @param newStock Yeni stok miktarı
     * @return Güncelleme başarılı mı
     */
    boolean updatePlatformProductStock(Long platformProductId, Integer newStock);
    
    /**
     * Platform ürün fiyatını günceller
     * @param platformProductId Platform ürün ID
     * @param newPrice Yeni fiyat
     * @return Güncelleme başarılı mı
     */
    boolean updatePlatformProductPrice(Long platformProductId, java.math.BigDecimal newPrice);
    
    /**
     * Stok değişikliğini tüm platformlara yansıtır
     * @param productId Ürün ID
     * @param stockChange Stok değişikliği (+ veya -)
     * @return Güncellenen platform sayısı
     */
    int propagateStockChange(Long productId, Integer stockChange);
    
    /**
     * Fiyat değişikliğini tüm platformlara yansıtır
     * @param productId Ürün ID
     * @param newPrice Yeni fiyat
     * @return Güncellenen platform sayısı
     */
    int propagatePriceChange(Long productId, java.math.BigDecimal newPrice);
    
    /**
     * Platform bağlantısını test eder
     * @param platformId Platform ID
     * @return Bağlantı başarılı mı
     */
    boolean testPlatformConnection(Long platformId);
    
    /**
     * Senkronizasyon durumunu kontrol eder
     * @return Senkronizasyon durumu raporu
     */
    Map<String, Object> getSyncStatus();
    
    /**
     * Başarısız senkronizasyonları tekrar dener
     * @return Tekrar denenilen işlem sayısı
     */
    int retryFailedSyncs();
    
    /**
     * Stok tutarlılığını kontrol eder
     * @return Tutarsızlık raporu
     */
    List<Map<String, Object>> checkStockConsistency();
}
