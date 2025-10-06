package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.StockMovement;
import com.ornek.ecomstocksync.entity.MaterialCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByMaterial(MaterialCard material);
    
    List<StockMovement> findByMaterialOrderByMovementDateDesc(MaterialCard material);
    
    List<StockMovement> findByMovementType(StockMovement.MovementType movementType);
    
    List<StockMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<StockMovement> findByMaterialAndMovementDateBetween(MaterialCard material, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.material = :material ORDER BY sm.movementDate DESC")
    List<StockMovement> findRecentMovementsByMaterial(@Param("material") MaterialCard material);
    
    @Query("SELECT SUM(sm.quantity) FROM StockMovement sm WHERE sm.material = :material AND sm.movementType IN ('INBOUND', 'PRODUCTION')")
    Double getTotalInboundQuantity(@Param("material") MaterialCard material);
    
    @Query("SELECT SUM(sm.quantity) FROM StockMovement sm WHERE sm.material = :material AND sm.movementType IN ('OUTBOUND', 'CONSUMPTION')")
    Double getTotalOutboundQuantity(@Param("material") MaterialCard material);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.reference = :reference")
    List<StockMovement> findByReference(@Param("reference") String reference);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.operator = :operator")
    List<StockMovement> findByOperator(@Param("operator") String operator);
    
    List<StockMovement> findByReferenceContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String reference, String description);
    
    @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate")
    Long countByMovementDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm.movementType, COUNT(sm) FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate GROUP BY sm.movementType")
    List<Object[]> countByMovementTypeAndDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
