package com.something.infrastructure.persistence.adapter;

import com.something.application.port.out.ProductRepositoryPort;
import com.something.domain.entity.ProductStatus;
import com.something.infrastructure.persistence.jpa.Product;
import com.something.infrastructure.persistence.jpa.ProductRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductRepository productRepository;

    public ProductRepositoryAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    public com.something.domain.entity.Product save(com.something.domain.entity.Product domainProduct) {
        Product entity = toEntity(domainProduct);
        Product saved = productRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<com.something.domain.entity.Product> findById(UUID id) {
        return productRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<com.something.domain.entity.Product> findAllByStatus(ProductStatus status) {
        return productRepository.findByStatus(status).stream().map(this::toDomain).toList();
    }

    @Override
    public List<com.something.domain.entity.Product> findAll() {
        return productRepository.findAll().stream().map(this::toDomain).toList();
    }

    private Product toEntity(com.something.domain.entity.Product domainProduct) {
        Product entity = new Product();
        entity.setId(domainProduct.getId());
        entity.setSku(domainProduct.getSku());
        entity.setName(domainProduct.getName());
        entity.setUnit(domainProduct.getUnit());
        entity.setSellingPrice(domainProduct.getSellingPrice());
        entity.setPurchasePrice(domainProduct.getPurchasePrice());
        entity.setStockQty(domainProduct.getStockQty());
        entity.setLowStockThreshold(domainProduct.getLowStockThreshold());
        entity.setStatus(domainProduct.getStatus() == null ? ProductStatus.ACTIVE : domainProduct.getStatus());
        entity.setVersion(0L);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    private com.something.domain.entity.Product toDomain(Product entity) {
        com.something.domain.entity.Product domainProduct = new com.something.domain.entity.Product();
        domainProduct.setId(entity.getId());
        domainProduct.setSku(entity.getSku());
        domainProduct.setName(entity.getName());
        domainProduct.setUnit(entity.getUnit());
        domainProduct.setSellingPrice(entity.getSellingPrice());
        domainProduct.setPurchasePrice(entity.getPurchasePrice());
        domainProduct.setStockQty(entity.getStockQty());
        domainProduct.setLowStockThreshold(entity.getLowStockThreshold());
        domainProduct.setStatus(entity.getStatus());
        return domainProduct;
    }
}
