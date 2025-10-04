package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.Supplier;
import com.ornek.ecomstocksync.entity.MaterialCard;

import java.util.List;
import java.util.Optional;
import java.util.Map;

public interface SupplierService {
    
    // Basic CRUD operations
    List<Supplier> findAll();
    
    Optional<Supplier> findById(Long id);
    
    Optional<Supplier> findBySupplierCode(String supplierCode);
    
    List<Supplier> findByStatus(String status);
    
    List<Supplier> findActiveSuppliers();
    
    Supplier save(Supplier supplier);
    
    Supplier update(Long id, Supplier supplier);
    
    void delete(Long id);
    
    boolean existsBySupplierCode(String supplierCode);
    
    boolean existsByEmail(String email);
    
    // Business operations
    List<Supplier> searchSuppliers(String keyword);
    
    List<Supplier> findByCity(String city);
    
    List<Supplier> findByCountry(String country);
    
    Map<String, Long> getSupplierStatistics();
    
    List<MaterialCard> getSupplierMaterials(Long supplierId);
    
    int getSupplierMaterialCount(Long supplierId);
    
    // Supplier performance metrics
    Map<String, Object> getSupplierPerformance(Long supplierId);
    
    // Supplier contact management
    void updateContactInfo(Long supplierId, String contactPerson, String email, String phone);
    
    // Supplier status management
    void activateSupplier(Long supplierId);
    
    void deactivateSupplier(Long supplierId);
    
    // Supplier validation
    boolean validateSupplier(Supplier supplier);
    
    // Supplier import/export
    List<Supplier> importSuppliers(List<Supplier> suppliers);
    
    String exportSuppliersToCsv();
}
