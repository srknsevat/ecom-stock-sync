package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.Supplier;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.repository.SupplierRepository;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private MaterialCardRepository materialRepository;
    
    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }
    
    @Override
    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }
    
    @Override
    public Optional<Supplier> findBySupplierCode(String supplierCode) {
        return supplierRepository.findBySupplierCode(supplierCode);
    }
    
    @Override
    public List<Supplier> findByStatus(String status) {
        return supplierRepository.findByStatus(status);
    }
    
    @Override
    public List<Supplier> findActiveSuppliers() {
        return supplierRepository.findByStatus("ACTIVE");
    }
    
    @Override
    public Supplier save(Supplier supplier) {
        if (supplier.getCreatedAt() == null) {
            supplier.setCreatedAt(LocalDateTime.now());
        }
        supplier.setUpdatedAt(LocalDateTime.now());
        return supplierRepository.save(supplier);
    }
    
    @Override
    public Supplier update(Long id, Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tedarikçi bulunamadı: " + id));
        
        // Update fields
        existingSupplier.setSupplierName(supplier.getSupplierName());
        existingSupplier.setContactPerson(supplier.getContactPerson());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setPhone(supplier.getPhone());
        existingSupplier.setFax(supplier.getFax());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setCity(supplier.getCity());
        existingSupplier.setCountry(supplier.getCountry());
        existingSupplier.setPostalCode(supplier.getPostalCode());
        existingSupplier.setTaxNumber(supplier.getTaxNumber());
        existingSupplier.setTaxOffice(supplier.getTaxOffice());
        existingSupplier.setWebsite(supplier.getWebsite());
        existingSupplier.setPaymentTerms(supplier.getPaymentTerms());
        existingSupplier.setDeliveryTerms(supplier.getDeliveryTerms());
        existingSupplier.setStatus(supplier.getStatus());
        existingSupplier.setUpdatedAt(LocalDateTime.now());
        
        return supplierRepository.save(existingSupplier);
    }
    
    @Override
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Tedarikçi bulunamadı: " + id);
        }
        supplierRepository.deleteById(id);
    }
    
    @Override
    public boolean existsBySupplierCode(String supplierCode) {
        return supplierRepository.existsBySupplierCode(supplierCode);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return supplierRepository.existsByEmail(email);
    }
    
    @Override
    public List<Supplier> searchSuppliers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        String searchTerm = "%" + keyword.toLowerCase() + "%";
        return supplierRepository.findBySupplierNameContainingIgnoreCaseOrSupplierCodeContainingIgnoreCase(
            searchTerm, searchTerm);
    }
    
    @Override
    public List<Supplier> findByCity(String city) {
        return supplierRepository.findByCityIgnoreCase(city);
    }
    
    @Override
    public List<Supplier> findByCountry(String country) {
        return supplierRepository.findByCountryIgnoreCase(country);
    }
    
    @Override
    public Map<String, Long> getSupplierStatistics() {
        Map<String, Long> stats = new HashMap<>();
        
        long totalSuppliers = supplierRepository.count();
        long activeSuppliers = supplierRepository.countByStatus("ACTIVE");
        long inactiveSuppliers = supplierRepository.countByStatus("INACTIVE");
        
        stats.put("totalSuppliers", totalSuppliers);
        stats.put("activeSuppliers", activeSuppliers);
        stats.put("inactiveSuppliers", inactiveSuppliers);
        
        return stats;
    }
    
    @Override
    public List<MaterialCard> getSupplierMaterials(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new IllegalArgumentException("Tedarikçi bulunamadı: " + supplierId));
        
        return materialRepository.findBySupplier(supplier);
    }
    
    @Override
    public int getSupplierMaterialCount(Long supplierId) {
        return getSupplierMaterials(supplierId).size();
    }
    
    @Override
    public Map<String, Object> getSupplierPerformance(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new IllegalArgumentException("Tedarikçi bulunamadı: " + supplierId));
        
        List<MaterialCard> materials = getSupplierMaterials(supplierId);
        
        Map<String, Object> performance = new HashMap<>();
        performance.put("supplierId", supplierId);
        performance.put("supplierName", supplier.getSupplierName());
        performance.put("materialCount", materials.size());
        
        if (!materials.isEmpty()) {
            // Calculate average cost
            double avgCost = materials.stream()
                .mapToDouble(m -> m.getAverageCost().doubleValue())
                .average()
                .orElse(0.0);
            performance.put("averageMaterialCost", avgCost);
            
            // Calculate total stock value
            double totalStockValue = materials.stream()
                .mapToDouble(m -> m.getStockValue().doubleValue())
                .sum();
            performance.put("totalStockValue", totalStockValue);
            
            // Count low stock materials
            long lowStockCount = materials.stream()
                .filter(MaterialCard::isLowStock)
                .count();
            performance.put("lowStockMaterials", lowStockCount);
        } else {
            performance.put("averageMaterialCost", 0.0);
            performance.put("totalStockValue", 0.0);
            performance.put("lowStockMaterials", 0);
        }
        
        return performance;
    }
    
    @Override
    public void updateContactInfo(Long supplierId, String contactPerson, String email, String phone) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new IllegalArgumentException("Tedarikçi bulunamadı: " + supplierId));
        
        supplier.setContactPerson(contactPerson);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setUpdatedAt(LocalDateTime.now());
        
        supplierRepository.save(supplier);
    }
    
    @Override
    public void activateSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new IllegalArgumentException("Tedarikçi bulunamadı: " + supplierId));
        
        supplier.setStatus("ACTIVE");
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }
    
    @Override
    public void deactivateSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new IllegalArgumentException("Tedarikçi bulunamadı: " + supplierId));
        
        supplier.setStatus("INACTIVE");
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepository.save(supplier);
    }
    
    @Override
    public boolean validateSupplier(Supplier supplier) {
        if (supplier == null) return false;
        if (supplier.getSupplierCode() == null || supplier.getSupplierCode().trim().isEmpty()) return false;
        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) return false;
        if (supplier.getEmail() != null && !isValidEmail(supplier.getEmail())) return false;
        if (supplier.getPhone() != null && !isValidPhone(supplier.getPhone())) return false;
        
        return true;
    }
    
    @Override
    public List<Supplier> importSuppliers(List<Supplier> suppliers) {
        List<Supplier> importedSuppliers = new ArrayList<>();
        
        for (Supplier supplier : suppliers) {
            if (validateSupplier(supplier) && !existsBySupplierCode(supplier.getSupplierCode())) {
                supplier.setCreatedAt(LocalDateTime.now());
                supplier.setUpdatedAt(LocalDateTime.now());
                importedSuppliers.add(supplierRepository.save(supplier));
            }
        }
        
        return importedSuppliers;
    }
    
    @Override
    public String exportSuppliersToCsv() {
        List<Supplier> suppliers = findAll();
        StringBuilder csv = new StringBuilder();
        
        // CSV Header
        csv.append("Supplier Code,Supplier Name,Contact Person,Email,Phone,City,Country,Status\n");
        
        // CSV Data
        for (Supplier supplier : suppliers) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                supplier.getSupplierCode(),
                supplier.getSupplierName(),
                supplier.getContactPerson() != null ? supplier.getContactPerson() : "",
                supplier.getEmail() != null ? supplier.getEmail() : "",
                supplier.getPhone() != null ? supplier.getPhone() : "",
                supplier.getCity() != null ? supplier.getCity() : "",
                supplier.getCountry() != null ? supplier.getCountry() : "",
                supplier.getStatus()
            ));
        }
        
        return csv.toString();
    }
    
    // Helper methods
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidPhone(String phone) {
        return phone.matches("^[+]?[0-9\\s\\-()]+$");
    }
}
