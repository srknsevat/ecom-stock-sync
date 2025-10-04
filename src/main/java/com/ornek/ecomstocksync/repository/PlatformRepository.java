package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    
    Optional<Platform> findByCode(String code);
    
    List<Platform> findByIsActiveTrue();
    
    List<Platform> findByType(Platform.PlatformType type);
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.id IN " +
           "(SELECT DISTINCT pp.platform.id FROM PlatformProduct pp WHERE pp.isActive = true)")
    List<Platform> findActivePlatformsWithProducts();
    
    boolean existsByCode(String code);
    
    boolean existsByName(String name);
}
