package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlatformService {
    
    List<Platform> getAllPlatforms();
    
    Optional<Platform> getPlatformById(Long id);
    
    Optional<Platform> getPlatformByCode(String code);
    
    List<Platform> getActivePlatforms();
    
    Platform createPlatform(Platform platform);
    
    Platform updatePlatform(Long id, Platform platform);
    
    void deletePlatform(Long id);
    
    boolean existsByCode(String code);
    
    boolean existsByName(String name);
    
    // Credential yönetimi
    void saveCredential(Long platformId, String credentialType, String credentialValue);
    
    String getCredential(Long platformId, String credentialType);
    
    void deleteCredential(Long platformId, String credentialType);
    
    List<String> getCredentialTypes(Long platformId);
    
    // Platform ürün yönetimi
    List<PlatformProduct> getPlatformProducts(Long platformId);
    
    PlatformProduct createPlatformProduct(Long platformId, Long productId, String platformProductId);
    
    PlatformProduct updatePlatformProduct(Long id, Map<String, Object> updates);
    
    void deletePlatformProduct(Long id);
    
    // Senkronizasyon
    List<Platform> getPlatformsNeedingSync();
    
    void markPlatformProductSynced(Long platformProductId);
    
    long countActiveProductsByPlatform(Long platformId);
}
