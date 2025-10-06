package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.BillOfMaterial;
import com.ornek.ecomstocksync.entity.MaterialCard;
import com.ornek.ecomstocksync.repository.BillOfMaterialRepository;
import com.ornek.ecomstocksync.repository.MaterialCardRepository;
import com.ornek.ecomstocksync.service.BillOfMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillOfMaterialServiceImpl implements BillOfMaterialService {
    
    @Autowired
    private BillOfMaterialRepository bomRepository;
    
    @Autowired
    private MaterialCardRepository materialRepository;
    
    @Override
    public List<BillOfMaterial> findAll() {
        return bomRepository.findAll();
    }
    
    @Override
    public Optional<BillOfMaterial> findById(Long id) {
        return bomRepository.findById(id);
    }
    
    @Override
    public BillOfMaterial save(BillOfMaterial bom) {
        return bomRepository.save(bom);
    }
    
    @Override
    public void deleteById(Long id) {
        bomRepository.deleteById(id);
    }
    
    @Override
    public List<BillOfMaterial> findByParentMaterial(MaterialCard parentMaterial) {
        return bomRepository.findByParentMaterial(parentMaterial);
    }
    
    @Override
    public List<BillOfMaterial> findByChildMaterial(MaterialCard childMaterial) {
        return bomRepository.findByChildMaterial(childMaterial);
    }
    
    @Override
    public List<BillOfMaterial> findActiveBomsByParent(MaterialCard parentMaterial) {
        return bomRepository.findActiveBomsByParent(parentMaterial);
    }
    
    @Override
    public List<BillOfMaterial> findActiveBomsByChild(MaterialCard childMaterial) {
        return bomRepository.findActiveBomsByChild(childMaterial);
    }
    
    @Override
    public Optional<BillOfMaterial> findActiveBomByParentAndChild(MaterialCard parent, MaterialCard child) {
        return Optional.ofNullable(bomRepository.findActiveBomByParentAndChild(parent, child));
    }
    
    @Override
    public List<BillOfMaterial> findActiveBomsByParentCategory(String category) {
        return bomRepository.findActiveBomsByParentCategory(category);
    }
    
    @Override
    public List<BillOfMaterial> findActiveBomsByWorkCenter(String workCenter) {
        return bomRepository.findActiveBomsByWorkCenter(workCenter);
    }
    
    @Override
    public List<BillOfMaterial> findActiveBomsByOperation(String operation) {
        return bomRepository.findActiveBomsByOperation(operation);
    }
    
    @Override
    public Long countActiveBomsByParent(MaterialCard parent) {
        return bomRepository.countActiveBomsByParent(parent);
    }
    
    @Override
    public Long countActiveBomsByChild(MaterialCard child) {
        return bomRepository.countActiveBomsByChild(child);
    }
    
    @Override
    public void createBom(MaterialCard parent, MaterialCard child, BigDecimal quantity, 
                         BigDecimal unitCost, String operation, String workCenter) {
        BillOfMaterial bom = new BillOfMaterial();
        bom.setParentMaterial(parent);
        bom.setChildMaterial(child);
        bom.setQuantity(quantity);
        bom.setUnitCost(unitCost);
        bom.setTotalCost(unitCost.multiply(quantity));
        bom.setOperation(operation);
        bom.setWorkCenter(workCenter);
        bom.setStatus("ACTIVE");
        bom.setEffectiveFrom(LocalDateTime.now());
        
        bomRepository.save(bom);
    }
    
    @Override
    public void updateBom(Long bomId, BigDecimal quantity, BigDecimal unitCost, 
                         String operation, String workCenter) {
        BillOfMaterial bom = bomRepository.findById(bomId)
            .orElseThrow(() -> new RuntimeException("BOM not found"));
        
        bom.setQuantity(quantity);
        bom.setUnitCost(unitCost);
        bom.setTotalCost(unitCost.multiply(quantity));
        bom.setOperation(operation);
        bom.setWorkCenter(workCenter);
        bom.setUpdatedAt(LocalDateTime.now());
        
        bomRepository.save(bom);
    }
    
    @Override
    public void activateBom(Long bomId) {
        BillOfMaterial bom = bomRepository.findById(bomId)
            .orElseThrow(() -> new RuntimeException("BOM not found"));
        
        bom.setStatus("ACTIVE");
        bom.setUpdatedAt(LocalDateTime.now());
        bomRepository.save(bom);
    }
    
    @Override
    public void deactivateBom(Long bomId) {
        BillOfMaterial bom = bomRepository.findById(bomId)
            .orElseThrow(() -> new RuntimeException("BOM not found"));
        
        bom.setStatus("INACTIVE");
        bom.setUpdatedAt(LocalDateTime.now());
        bomRepository.save(bom);
    }
    
    @Override
    public void setEffectivePeriod(Long bomId, LocalDateTime effectiveFrom, LocalDateTime effectiveTo) {
        BillOfMaterial bom = bomRepository.findById(bomId)
            .orElseThrow(() -> new RuntimeException("BOM not found"));
        
        bom.setEffectiveFrom(effectiveFrom);
        bom.setEffectiveTo(effectiveTo);
        bom.setUpdatedAt(LocalDateTime.now());
        
        bomRepository.save(bom);
    }
    
    @Override
    public Map<MaterialCard, BigDecimal> explodeBom(MaterialCard parentMaterial, BigDecimal quantity) {
        Map<MaterialCard, BigDecimal> explosion = new HashMap<>();
        explodeBomRecursive(parentMaterial, quantity, explosion, new HashSet<>(), 0);
        return explosion;
    }
    
    @Override
    public Map<MaterialCard, BigDecimal> explodeBomWithScrap(MaterialCard parentMaterial, BigDecimal quantity) {
        Map<MaterialCard, BigDecimal> explosion = new HashMap<>();
        explodeBomWithScrapRecursive(parentMaterial, quantity, explosion, new HashSet<>(), 0);
        return explosion;
    }
    
    @Override
    public List<BomExplosionResult> getDetailedBomExplosion(MaterialCard parentMaterial, BigDecimal quantity) {
        List<BomExplosionResult> results = new ArrayList<>();
        explodeBomDetailed(parentMaterial, quantity, results, new HashSet<>(), 0);
        return results;
    }
    
    @Override
    public BigDecimal calculateBomCost(MaterialCard parentMaterial) {
        Map<MaterialCard, BigDecimal> explosion = explodeBom(parentMaterial, BigDecimal.ONE);
        return explosion.entrySet().stream()
            .map(entry -> entry.getKey().getAverageCost().multiply(entry.getValue()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public BigDecimal calculateBomCostWithQuantity(MaterialCard parentMaterial, BigDecimal quantity) {
        return calculateBomCost(parentMaterial).multiply(quantity);
    }
    
    @Override
    public Map<MaterialCard, BigDecimal> calculateComponentCosts(MaterialCard parentMaterial, BigDecimal quantity) {
        Map<MaterialCard, BigDecimal> explosion = explodeBom(parentMaterial, quantity);
        return explosion.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getKey().getAverageCost().multiply(entry.getValue())
            ));
    }
    
    @Override
    public BigDecimal calculateTotalOperationTime(MaterialCard parentMaterial) {
        List<BillOfMaterial> boms = findActiveBomsByParent(parentMaterial);
        return boms.stream()
            .map(BillOfMaterial::getOperationTime)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public BigDecimal calculateTotalOperationTimeWithQuantity(MaterialCard parentMaterial, BigDecimal quantity) {
        return calculateTotalOperationTime(parentMaterial).multiply(quantity);
    }
    
    @Override
    public Map<String, BigDecimal> calculateWorkCenterTimes(MaterialCard parentMaterial, BigDecimal quantity) {
        List<BillOfMaterial> boms = findActiveBomsByParent(parentMaterial);
        return boms.stream()
            .filter(bom -> bom.getWorkCenter() != null && bom.getOperationTime() != null)
            .collect(Collectors.groupingBy(
                BillOfMaterial::getWorkCenter,
                Collectors.reducing(BigDecimal.ZERO, 
                    bom -> bom.getOperationTime().multiply(quantity),
                    BigDecimal::add)
            ));
    }
    
    @Override
    public boolean validateBomStructure(MaterialCard parentMaterial) {
        return getBomValidationErrors(parentMaterial).isEmpty();
    }
    
    @Override
    public List<String> getBomValidationErrors(MaterialCard parentMaterial) {
        List<String> errors = new ArrayList<>();
        
        // Check for circular dependencies
        if (hasCircularDependency(parentMaterial, parentMaterial)) {
            errors.add("Circular dependency detected");
        }
        
        // Check for missing components
        List<BillOfMaterial> boms = findActiveBomsByParent(parentMaterial);
        for (BillOfMaterial bom : boms) {
            if (bom.getChildMaterial() == null) {
                errors.add("Missing child material in BOM");
            }
            if (bom.getQuantity() == null || bom.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Invalid quantity in BOM");
            }
        }
        
        return errors;
    }
    
    @Override
    public boolean hasCircularDependency(MaterialCard parentMaterial, MaterialCard childMaterial) {
        Set<MaterialCard> visited = new HashSet<>();
        return hasCircularDependencyRecursive(parentMaterial, childMaterial, visited);
    }
    
    @Override
    public List<BomReport> getBomReports() {
        List<MaterialCard> parentMaterials = materialRepository.findAll().stream()
            .filter(material -> !findActiveBomsByParent(material).isEmpty())
            .collect(Collectors.toList());
        
        return parentMaterials.stream()
            .map(this::createBomReport)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BomReport> getBomReportsByCategory(String category) {
        List<MaterialCard> parentMaterials = materialRepository.findByCategory(category).stream()
            .filter(material -> !findActiveBomsByParent(material).isEmpty())
            .collect(Collectors.toList());
        
        return parentMaterials.stream()
            .map(this::createBomReport)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BomReport> getBomReportsByWorkCenter(String workCenter) {
        List<BillOfMaterial> boms = findActiveBomsByWorkCenter(workCenter);
        Set<MaterialCard> parentMaterials = boms.stream()
            .map(BillOfMaterial::getParentMaterial)
            .collect(Collectors.toSet());
        
        return parentMaterials.stream()
            .map(this::createBomReport)
            .collect(Collectors.toList());
    }
    
    // Private helper methods
    private void explodeBomRecursive(MaterialCard parent, BigDecimal quantity, 
                                   Map<MaterialCard, BigDecimal> explosion, 
                                   Set<MaterialCard> visited, int level) {
        if (level > 10) return; // Prevent infinite recursion
        
        List<BillOfMaterial> boms = findActiveBomsByParent(parent);
        for (BillOfMaterial bom : boms) {
            MaterialCard child = bom.getChildMaterial();
            BigDecimal requiredQuantity = bom.getEffectiveQuantity().multiply(quantity);
            
            explosion.merge(child, requiredQuantity, BigDecimal::add);
            
            if (!visited.contains(child)) {
                visited.add(child);
                explodeBomRecursive(child, requiredQuantity, explosion, visited, level + 1);
            }
        }
    }
    
    private void explodeBomWithScrapRecursive(MaterialCard parent, BigDecimal quantity, 
                                            Map<MaterialCard, BigDecimal> explosion, 
                                            Set<MaterialCard> visited, int level) {
        if (level > 10) return;
        
        List<BillOfMaterial> boms = findActiveBomsByParent(parent);
        for (BillOfMaterial bom : boms) {
            MaterialCard child = bom.getChildMaterial();
            BigDecimal effectiveQuantity = bom.getEffectiveQuantity();
            BigDecimal requiredQuantity = effectiveQuantity.multiply(quantity);
            
            explosion.merge(child, requiredQuantity, BigDecimal::add);
            
            if (!visited.contains(child)) {
                visited.add(child);
                explodeBomWithScrapRecursive(child, requiredQuantity, explosion, visited, level + 1);
            }
        }
    }
    
    private void explodeBomDetailed(MaterialCard parent, BigDecimal quantity, 
                                  List<BomExplosionResult> results, 
                                  Set<MaterialCard> visited, int level) {
        if (level > 10) return;
        
        List<BillOfMaterial> boms = findActiveBomsByParent(parent);
        for (BillOfMaterial bom : boms) {
            MaterialCard child = bom.getChildMaterial();
            BigDecimal requiredQuantity = bom.getEffectiveQuantity().multiply(quantity);
            BigDecimal availableQuantity = child.getCurrentStock();
            BigDecimal shortage = requiredQuantity.subtract(availableQuantity).max(BigDecimal.ZERO);
            BigDecimal cost = child.getAverageCost().multiply(requiredQuantity);
            
            BomExplosionResult result = new BomExplosionResult(
                child, requiredQuantity, availableQuantity, shortage, cost, level
            );
            results.add(result);
            
            if (!visited.contains(child)) {
                visited.add(child);
                explodeBomDetailed(child, requiredQuantity, results, visited, level + 1);
            }
        }
    }
    
    private boolean hasCircularDependencyRecursive(MaterialCard current, MaterialCard target, 
                                                 Set<MaterialCard> visited) {
        if (current.equals(target)) return true;
        if (visited.contains(current)) return false;
        
        visited.add(current);
        List<BillOfMaterial> boms = findActiveBomsByParent(current);
        
        for (BillOfMaterial bom : boms) {
            if (hasCircularDependencyRecursive(bom.getChildMaterial(), target, visited)) {
                return true;
            }
        }
        
        return false;
    }
    
    private BomReport createBomReport(MaterialCard parentMaterial) {
        List<BillOfMaterial> boms = findActiveBomsByParent(parentMaterial);
        int componentCount = boms.size();
        BigDecimal totalCost = calculateBomCost(parentMaterial);
        BigDecimal totalTime = calculateTotalOperationTime(parentMaterial);
        String status = boms.stream().allMatch(bom -> "ACTIVE".equals(bom.getStatus())) ? "ACTIVE" : "PARTIAL";
        LocalDateTime lastUpdated = boms.stream()
            .map(BillOfMaterial::getUpdatedAt)
            .filter(Objects::nonNull)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
        
        return new BomReport(parentMaterial, componentCount, totalCost, totalTime, status, lastUpdated);
    }
}
