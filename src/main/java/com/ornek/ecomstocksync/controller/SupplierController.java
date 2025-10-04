package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.Supplier;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    // Basic CRUD operations
    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierService.findById(id);
        return supplier.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{supplierCode}")
    public ResponseEntity<Supplier> getSupplierByCode(@PathVariable String supplierCode) {
        Optional<Supplier> supplier = supplierService.findBySupplierCode(supplierCode);
        return supplier.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) {
        if (supplierService.existsBySupplierCode(supplier.getSupplierCode())) {
            return ResponseEntity.badRequest().build();
        }
        
        if (!supplierService.validateSupplier(supplier)) {
            return ResponseEntity.badRequest().build();
        }
        
        Supplier savedSupplier = supplierService.save(supplier);
        return ResponseEntity.ok(savedSupplier);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        try {
            Supplier updatedSupplier = supplierService.update(id, supplier);
            return ResponseEntity.ok(updatedSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.delete(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Search and filter operations
    @GetMapping("/search")
    public List<Supplier> searchSuppliers(@RequestParam String keyword) {
        return supplierService.searchSuppliers(keyword);
    }
    
    @GetMapping("/status/{status}")
    public List<Supplier> getSuppliersByStatus(@PathVariable String status) {
        return supplierService.findByStatus(status);
    }
    
    @GetMapping("/active")
    public List<Supplier> getActiveSuppliers() {
        return supplierService.findActiveSuppliers();
    }
    
    @GetMapping("/city/{city}")
    public List<Supplier> getSuppliersByCity(@PathVariable String city) {
        return supplierService.findByCity(city);
    }
    
    @GetMapping("/country/{country}")
    public List<Supplier> getSuppliersByCountry(@PathVariable String country) {
        return supplierService.findByCountry(country);
    }
    
    // Statistics and analytics
    @GetMapping("/statistics")
    public Map<String, Long> getSupplierStatistics() {
        return supplierService.getSupplierStatistics();
    }
    
    @GetMapping("/{id}/materials")
    public List<MaterialCard> getSupplierMaterials(@PathVariable Long id) {
        return supplierService.getSupplierMaterials(id);
    }
    
    @GetMapping("/{id}/materials/count")
    public ResponseEntity<Integer> getSupplierMaterialCount(@PathVariable Long id) {
        try {
            int count = supplierService.getSupplierMaterialCount(id);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/performance")
    public ResponseEntity<Map<String, Object>> getSupplierPerformance(@PathVariable Long id) {
        try {
            Map<String, Object> performance = supplierService.getSupplierPerformance(id);
            return ResponseEntity.ok(performance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Contact management
    @PatchMapping("/{id}/contact")
    public ResponseEntity<Void> updateContactInfo(@PathVariable Long id,
                                                @RequestParam String contactPerson,
                                                @RequestParam String email,
                                                @RequestParam String phone) {
        try {
            supplierService.updateContactInfo(id, contactPerson, email, phone);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Status management
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateSupplier(@PathVariable Long id) {
        try {
            supplierService.activateSupplier(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSupplier(@PathVariable Long id) {
        try {
            supplierService.deactivateSupplier(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Validation
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSupplier(@RequestBody Supplier supplier) {
        boolean isValid = supplierService.validateSupplier(supplier);
        Map<String, Object> result = Map.of(
            "valid", isValid,
            "supplierCode", supplier.getSupplierCode(),
            "supplierName", supplier.getSupplierName()
        );
        return ResponseEntity.ok(result);
    }
    
    // Import/Export
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importSuppliers(@RequestBody List<Supplier> suppliers) {
        List<Supplier> importedSuppliers = supplierService.importSuppliers(suppliers);
        Map<String, Object> result = Map.of(
            "importedCount", importedSuppliers.size(),
            "totalCount", suppliers.size(),
            "importedSuppliers", importedSuppliers
        );
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportSuppliersToCsv() {
        String csvData = supplierService.exportSuppliersToCsv();
        return ResponseEntity.ok()
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=suppliers.csv")
            .body(csvData);
    }
    
    // Dashboard endpoints
    @GetMapping("/dashboard/summary")
    public Map<String, Object> getSupplierDashboardSummary() {
        Map<String, Long> stats = supplierService.getSupplierStatistics();
        List<Supplier> recentSuppliers = supplierService.findAll().stream()
            .sorted((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()))
            .limit(5)
            .toList();
        
        return Map.of(
            "statistics", stats,
            "recentSuppliers", recentSuppliers,
            "totalSuppliers", stats.get("totalSuppliers"),
            "activeSuppliers", stats.get("activeSuppliers"),
            "inactiveSuppliers", stats.get("inactiveSuppliers")
        );
    }
}
