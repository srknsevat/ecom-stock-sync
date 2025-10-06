package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.service.StockSyncService;
import com.ornek.ecomstocksync.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/platforms")
@CrossOrigin(origins = "*")
public class PlatformController {
    
    @Autowired
    private PlatformService platformService;

    @Autowired
    private StockSyncService stockSyncService;
    
    @GetMapping
    public ResponseEntity<List<Platform>> getAllPlatforms() {
        List<Platform> platforms = platformService.getAllPlatforms();
        return ResponseEntity.ok(platforms);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Platform>> getActivePlatforms() {
        List<Platform> platforms = platformService.getActivePlatforms();
        return ResponseEntity.ok(platforms);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Platform> getPlatformById(@PathVariable Long id) {
        Optional<Platform> platform = platformService.getPlatformById(id);
        return platform.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Platform> getPlatformByCode(@PathVariable String code) {
        Optional<Platform> platform = platformService.getPlatformByCode(code);
        return platform.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Platform> createPlatform(@Valid @RequestBody Platform platform) {
        try {
            Platform createdPlatform = platformService.createPlatform(platform);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlatform);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Platform> updatePlatform(@PathVariable Long id, @Valid @RequestBody Platform platform) {
        try {
            Platform updatedPlatform = platformService.updatePlatform(id, platform);
            return ResponseEntity.ok(updatedPlatform);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        try {
            platformService.deletePlatform(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/products")
    public ResponseEntity<List<com.ornek.ecomstocksync.entity.PlatformProduct>> getPlatformProducts(@PathVariable Long id) {
        try {
            List<com.ornek.ecomstocksync.entity.PlatformProduct> products = platformService.getPlatformProducts(id);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/products")
    public ResponseEntity<com.ornek.ecomstocksync.entity.PlatformProduct> createPlatformProduct(
            @PathVariable Long id,
            @Valid @RequestBody com.ornek.ecomstocksync.dto.PlatformProductCreateRequest req) {
        try {
            Long productId = req.getProductId();
            String platformProductId = req.getPlatformProductId();
            com.ornek.ecomstocksync.entity.PlatformProduct platformProduct =
                platformService.createPlatformProduct(id, productId, platformProductId);
            return ResponseEntity.status(HttpStatus.CREATED).body(platformProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PutMapping("/products/{productId}")
    public ResponseEntity<com.ornek.ecomstocksync.entity.PlatformProduct> updatePlatformProduct(
            @PathVariable Long productId, 
            @RequestBody Map<String, Object> updates) {
        try {
            com.ornek.ecomstocksync.entity.PlatformProduct platformProduct = 
                platformService.updatePlatformProduct(productId, updates);
            return ResponseEntity.ok(platformProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deletePlatformProduct(@PathVariable Long productId) {
        try {
            platformService.deletePlatformProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/credentials")
    public ResponseEntity<List<String>> getCredentialTypes(@PathVariable Long id) {
        try {
            List<String> credentialTypes = platformService.getCredentialTypes(id);
            return ResponseEntity.ok(credentialTypes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/credentials")
    public ResponseEntity<Void> saveCredential(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String credentialType = request.get("credentialType");
            String credentialValue = request.get("credentialValue");
            
            platformService.saveCredential(id, credentialType, credentialValue);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/credentials/{credentialType}")
    public ResponseEntity<Void> deleteCredential(
            @PathVariable Long id,
            @PathVariable String credentialType) {
        try {
            platformService.deleteCredential(id, credentialType);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getPlatformStats(@PathVariable Long id) {
        try {
            long productCount = platformService.countActiveProductsByPlatform(id);
            Map<String, Object> stats = Map.of(
                "productCount", productCount,
                "isActive", platformService.getPlatformById(id).map(Platform::isActive).orElse(false)
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{id}/test")
    public ResponseEntity<Map<String,Object>> testPlatform(@PathVariable Long id) {
        try {
            boolean connected = stockSyncService.testPlatformConnection(id);
            String apiKey = platformService.getCredential(id, "API_KEY");
            boolean hasCred = apiKey != null && !apiKey.isBlank();
            Map<String,Object> res = Map.of(
                "success", connected && hasCred,
                "connected", connected,
                "hasCredential", hasCred
            );
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
