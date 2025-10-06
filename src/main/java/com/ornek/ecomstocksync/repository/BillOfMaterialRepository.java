package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.BillOfMaterial;
import com.ornek.ecomstocksync.entity.MaterialCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {
    
    List<BillOfMaterial> findByParentMaterial(MaterialCard parentMaterial);
    
    List<BillOfMaterial> findByChildMaterial(MaterialCard childMaterial);
    
    List<BillOfMaterial> findByParentMaterialAndStatus(MaterialCard parentMaterial, String status);
    
    List<BillOfMaterial> findByChildMaterialAndStatus(MaterialCard childMaterial, String status);
    
    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.parentMaterial = :parent AND bom.status = 'ACTIVE'")
    List<BillOfMaterial> findActiveBomsByParent(@Param("parent") MaterialCard parent);
    
    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.childMaterial = :child AND bom.status = 'ACTIVE'")
    List<BillOfMaterial> findActiveBomsByChild(@Param("child") MaterialCard child);
    
    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.parentMaterial = :parent AND bom.childMaterial = :child AND bom.status = 'ACTIVE'")
    BillOfMaterial findActiveBomByParentAndChild(@Param("parent") MaterialCard parent, @Param("child") MaterialCard child);
    
    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.parentMaterial.category = :category AND bom.status = 'ACTIVE'")
    List<BillOfMaterial> findActiveBomsByParentCategory(@Param("category") String category);
    
    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.workCenter = :workCenter AND bom.status = 'ACTIVE'")
    List<BillOfMaterial> findActiveBomsByWorkCenter(@Param("workCenter") String workCenter);
    
    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.operation = :operation AND bom.status = 'ACTIVE'")
    List<BillOfMaterial> findActiveBomsByOperation(@Param("operation") String operation);
    
    @Query("SELECT COUNT(bom) FROM BillOfMaterial bom WHERE bom.parentMaterial = :parent AND bom.status = 'ACTIVE'")
    Long countActiveBomsByParent(@Param("parent") MaterialCard parent);
    
    @Query("SELECT COUNT(bom) FROM BillOfMaterial bom WHERE bom.childMaterial = :child AND bom.status = 'ACTIVE'")
    Long countActiveBomsByChild(@Param("child") MaterialCard child);
}
