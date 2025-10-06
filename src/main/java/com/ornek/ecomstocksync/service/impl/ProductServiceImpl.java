package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.entity.Product;
import com.ornek.ecomstocksync.repository.ProductRepository;
import com.ornek.ecomstocksync.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() { return productRepository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) { return productRepository.findById(id); }

    @Override
    public Product save(Product p) { return productRepository.save(p); }

    @Override
    public void delete(Long id) { productRepository.deleteById(id); }

    @Override
    public void adjustStock(Long id, BigDecimal delta) {
        Product p = productRepository.findById(id).orElseThrow();
        p.setStock(p.getStock().add(delta));
        productRepository.save(p);
    }
}
