package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialCardRepository extends JpaRepository<MaterialCard, Long> {
    
    Optional<MaterialCard> findByMaterialCode(String materialCode);
    
    List<MaterialCard> findByCategory(String category);
    
    List<MaterialCard> findByStatus(String status);
    
    List<MaterialCard> findBySupplier(Supplier supplier);
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock <= m.minimumStock")
    List<MaterialCard> findLowStockMaterials();
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock >= m.maximumStock AND m.maximumStock > 0")
    List<MaterialCard> findOverStockMaterials();
    
    @Query("SELECT m FROM MaterialCard m WHERE m.materialName LIKE %:name% OR m.materialCode LIKE %:name%")
    List<MaterialCard> findByNameOrCodeContaining(@Param("name") String name);
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock > 0")
    List<MaterialCard> findInStockMaterials();
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock = 0")
    List<MaterialCard> findOutOfStockMaterials();
    
    @Query("SELECT SUM(m.currentStock * m.averageCost) FROM MaterialCard m")
    BigDecimal getTotalStockValue();
    
    @Query("SELECT COUNT(m) FROM MaterialCard m WHERE m.status = 'ACTIVE'")
    Long countActiveMaterials();
    
    @Query("SELECT COUNT(m) FROM MaterialCard m WHERE m.currentStock <= m.minimumStock")
    Long countLowStockMaterials();
}
