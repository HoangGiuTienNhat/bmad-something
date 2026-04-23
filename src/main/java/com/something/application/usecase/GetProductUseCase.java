package com.something.application.usecase;

import com.something.application.port.out.ProductRepositoryPort;
import com.something.domain.entity.Product;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public GetProductUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product execute(UUID id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(UUID id) {
            super("Product not found: " + id);
        }
    }
}
