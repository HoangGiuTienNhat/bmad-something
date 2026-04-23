package com.something.application.usecase;

import com.something.application.port.out.ProductRepositoryPort;
import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListProductsUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public ListProductsUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public List<Product> execute(ProductStatus status) {
        if (status != null) {
            return productRepositoryPort.findAllByStatus(status);
        }
        return productRepositoryPort.findAll();
    }
}
