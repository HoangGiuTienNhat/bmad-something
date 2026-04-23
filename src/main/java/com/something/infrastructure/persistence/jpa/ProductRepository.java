package com.something.infrastructure.persistence.jpa;

import com.something.domain.entity.ProductStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySku(String sku);

    List<Product> findByStatus(ProductStatus status);
}
