package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.Platform;
import com.ornek.ecomstocksync.entity.PlatformProduct;
import com.ornek.ecomstocksync.entity.MaterialCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformProductRepository extends JpaRepository<PlatformProduct, Long> {
    
    List<PlatformProduct> findByPlatform(Platform platform);
    
    List<PlatformProduct> findByProduct(MaterialCard material);
    List<PlatformProduct> findByMaterial(MaterialCard material);
    
    Optional<PlatformProduct> findByPlatformAndMaterial(Platform platform, MaterialCard material);
    
    Optional<PlatformProduct> findByPlatformAndPlatformProductId(Platform platform, String platformProductId);
    
    List<PlatformProduct> findByIsActiveTrue();
    
    List<PlatformProduct> findByPlatformAndIsActiveTrue(Platform platform);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform = :platform AND pp.isActive = true " +
           "AND (pp.lastSyncAt IS NULL OR pp.lastSyncAt < :before)")
    List<PlatformProduct> findStaleProducts(Platform platform, LocalDateTime before);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.platform = :platform AND pp.isActive = true")
    long countActiveProductsByPlatform(Platform platform);
}
