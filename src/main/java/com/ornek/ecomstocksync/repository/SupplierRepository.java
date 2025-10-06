package com.ornek.ecomstocksync.repository;

import com.ornek.ecomstocksync.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    Optional<Supplier> findBySupplierCode(String supplierCode);
    
    List<Supplier> findByStatus(String status);
    
    List<Supplier> findByCountryIgnoreCase(String country);
    
    List<Supplier> findByCityIgnoreCase(String city);
    
    boolean existsBySupplierCode(String supplierCode);
    
    boolean existsByEmail(String email);
    
    long countByStatus(String status);
    
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.supplierName) LIKE LOWER(:name) OR LOWER(s.supplierCode) LIKE LOWER(:name)")
    List<Supplier> findBySupplierNameContainingIgnoreCaseOrSupplierCodeContainingIgnoreCase(@Param("name") String name, @Param("name") String name2);
    
    @Query("SELECT s FROM Supplier s WHERE s.contactPerson LIKE %:contact%")
    List<Supplier> findByContactPersonContaining(@Param("contact") String contact);
    
    @Query("SELECT s FROM Supplier s WHERE s.email LIKE %:email%")
    List<Supplier> findByEmailContaining(@Param("email") String email);
    
    @Query("SELECT s FROM Supplier s WHERE s.status = 'ACTIVE'")
    List<Supplier> findActiveSuppliers();
    
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.status = 'ACTIVE'")
    Long countActiveSuppliers();
    
    @Query("SELECT s FROM Supplier s WHERE s.paymentTerms = :paymentTerms")
    List<Supplier> findByPaymentTerms(@Param("paymentTerms") String paymentTerms);
    
    @Query("SELECT s FROM Supplier s WHERE s.deliveryTerms = :deliveryTerms")
    List<Supplier> findByDeliveryTerms(@Param("deliveryTerms") String deliveryTerms);
}
