package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.Credential;
import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.repository.CredentialRepository;
import com.ornek.ecomstocksync.repository.PlatformProductRepository;
import com.ornek.ecomstocksync.repository.PlatformRepository;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.service.CredentialEncryptionService;
import com.ornek.ecomstocksync.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PlatformServiceImpl implements PlatformService {
    
    @Autowired
    private PlatformRepository platformRepository;
    
    @Autowired
    private PlatformProductRepository platformProductRepository;
    
    @Autowired
    private CredentialRepository credentialRepository;
    
    @Autowired
    private MaterialCardRepository materialRepository;
    
    @Autowired
    private CredentialEncryptionService credentialEncryptionService;
    
    @Override
    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }
    
    @Override
    public Optional<Platform> getPlatformById(Long id) {
        return platformRepository.findById(id);
    }
    
    @Override
    public Optional<Platform> getPlatformByCode(String code) {
        return platformRepository.findByCode(code);
    }
    
    @Override
    public List<Platform> getActivePlatforms() {
        return platformRepository.findByIsActiveTrue();
    }
    
    @Override
    public Platform createPlatform(Platform platform) {
        if (platformRepository.existsByCode(platform.getCode())) {
            throw new IllegalArgumentException("Bu kod ile bir platform zaten mevcut: " + platform.getCode());
        }
        if (platformRepository.existsByName(platform.getName())) {
            throw new IllegalArgumentException("Bu isim ile bir platform zaten mevcut: " + platform.getName());
        }
        return platformRepository.save(platform);
    }
    
    @Override
    public Platform updatePlatform(Long id, Platform platform) {
        Platform existingPlatform = platformRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + id));
        
        // Kod değişikliği kontrolü
        if (!existingPlatform.getCode().equals(platform.getCode()) && 
            platformRepository.existsByCode(platform.getCode())) {
            throw new IllegalArgumentException("Bu kod ile bir platform zaten mevcut: " + platform.getCode());
        }
        
        // İsim değişikliği kontrolü
        if (!existingPlatform.getName().equals(platform.getName()) && 
            platformRepository.existsByName(platform.getName())) {
            throw new IllegalArgumentException("Bu isim ile bir platform zaten mevcut: " + platform.getName());
        }
        
        existingPlatform.setName(platform.getName());
        existingPlatform.setCode(platform.getCode());
        existingPlatform.setDescription(platform.getDescription());
        existingPlatform.setType(platform.getType());
        existingPlatform.setBaseUrl(platform.getBaseUrl());
        existingPlatform.setWebhookUrl(platform.getWebhookUrl());
        existingPlatform.setActive(platform.isActive());
        
        return platformRepository.save(existingPlatform);
    }
    
    @Override
    public void deletePlatform(Long id) {
        if (!platformRepository.existsById(id)) {
            throw new IllegalArgumentException("Platform bulunamadı: " + id);
        }
        platformRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByCode(String code) {
        return platformRepository.existsByCode(code);
    }
    
    @Override
    public boolean existsByName(String name) {
        return platformRepository.existsByName(name);
    }
    
    @Override
    public void saveCredential(Long platformId, String credentialType, String credentialValue) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        String encryptedValue = credentialEncryptionService.encrypt(credentialValue);
        
        Optional<Credential> existingCredential = credentialRepository
            .findByPlatformAndCredentialType(platform, credentialType);
        
        if (existingCredential.isPresent()) {
            Credential credential = existingCredential.get();
            credential.setEncryptedValue(encryptedValue);
            credential.setActive(true);
            credentialRepository.save(credential);
        } else {
            Credential credential = new Credential(platform, credentialType, encryptedValue);
            credentialRepository.save(credential);
        }
    }
    
    @Override
    public String getCredential(Long platformId, String credentialType) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        Optional<Credential> credential = credentialRepository
            .findActiveCredential(platform, credentialType, LocalDateTime.now());
        
        if (credential.isPresent()) {
            return credentialEncryptionService.decrypt(credential.get().getEncryptedValue());
        }
        
        return null;
    }
    
    @Override
    public void deleteCredential(Long platformId, String credentialType) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        Optional<Credential> credential = credentialRepository
            .findByPlatformAndCredentialType(platform, credentialType);
        
        if (credential.isPresent()) {
            credentialRepository.delete(credential.get());
        }
    }
    
    @Override
    public List<String> getCredentialTypes(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        return credentialRepository.findByPlatformAndIsActiveTrue(platform)
            .stream()
            .map(Credential::getCredentialType)
            .toList();
    }
    
    @Override
    public List<PlatformProduct> getPlatformProducts(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        return platformProductRepository.findByPlatform(platform);
    }
    
    @Override
    public PlatformProduct createPlatformProduct(Long platformId, Long productId, String platformProductId) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        MaterialCard material = materialRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Malzeme bulunamadı: " + productId));
        
        PlatformProduct platformProduct = new PlatformProduct(platform, material, platformProductId);
        platformProduct.setPlatformSku(material.getMaterialCode());
        platformProduct.setPrice(material.getAverageCost());
        platformProduct.setStock(material.getCurrentStock().intValue());
        
        return platformProductRepository.save(platformProduct);
    }
    
    @Override
    public PlatformProduct updatePlatformProduct(Long id, Map<String, Object> updates) {
        PlatformProduct platformProduct = platformProductRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Platform ürün bulunamadı: " + id));
        
        updates.forEach((key, value) -> {
            switch (key) {
                case "platformSku" -> platformProduct.setPlatformSku((String) value);
                case "price" -> platformProduct.setPrice((java.math.BigDecimal) value);
                case "stock" -> platformProduct.setStock((Integer) value);
                case "isActive" -> platformProduct.setActive((Boolean) value);
            }
        });
        
        return platformProductRepository.save(platformProduct);
    }
    
    @Override
    public void deletePlatformProduct(Long id) {
        if (!platformProductRepository.existsById(id)) {
            throw new IllegalArgumentException("Platform ürün bulunamadı: " + id);
        }
        platformProductRepository.deleteById(id);
    }
    
    @Override
    public List<Platform> getPlatformsNeedingSync() {
        return platformRepository.findActivePlatformsWithProducts();
    }
    
    @Override
    public void markPlatformProductSynced(Long platformProductId) {
        PlatformProduct platformProduct = platformProductRepository.findById(platformProductId)
            .orElseThrow(() -> new IllegalArgumentException("Platform ürün bulunamadı: " + platformProductId));
        
        platformProduct.setLastSyncAt(LocalDateTime.now());
        platformProductRepository.save(platformProduct);
    }
    
    @Override
    public long countActiveProductsByPlatform(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı: " + platformId));
        
        return platformProductRepository.countActiveProductsByPlatform(platform);
    }
}
