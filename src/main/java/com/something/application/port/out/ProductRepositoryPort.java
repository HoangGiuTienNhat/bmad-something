package com.something.application.port.out;

import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    boolean existsBySku(String sku);

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAllByStatus(ProductStatus status);

    List<Product> findAll();
}
