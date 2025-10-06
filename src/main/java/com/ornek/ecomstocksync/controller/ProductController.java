package com.ornek.ecomstocksync.controller;

import com.ornek.ecomstocksync.entity.Product;
import com.ornek.ecomstocksync.service.ProductService;
import com.ornek.ecomstocksync.service.StockSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final StockSyncService stockSyncService;

    private final ProductService productService;

    public ProductController(ProductService productService, StockSyncService stockSyncService) { this.productService = productService; this.stockSyncService = stockSyncService; }

    @GetMapping
    public List<Product> list() { return productService.findAll(); }

    @PostMapping
    public Product create(@RequestBody Product p) { return productService.save(p); }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<java.util.Map<String, Object>> adjust(@PathVariable Long id, @RequestParam BigDecimal delta) {
        productService.adjustStock(id, delta);
        int updated = stockSyncService.propagateStockChange(id, delta.intValue());
        return ResponseEntity.ok(java.util.Map.of("success", true, "updatedPlatforms", updated));
    }
}
