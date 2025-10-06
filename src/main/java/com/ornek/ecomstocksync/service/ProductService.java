package com.ornek.ecomstocksync.service;

import com.ornek.ecomstocksync.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product p);
    void delete(Long id);
    void adjustStock(Long id, BigDecimal delta);
}
