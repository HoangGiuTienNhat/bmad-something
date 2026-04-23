package com.something.application.usecase;

import com.something.application.port.out.ProductRepositoryPort;
import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public CreateProductUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product execute(Command command) {
        if (productRepositoryPort.existsBySku(command.sku())) {
            throw new IllegalArgumentException("SKU must be unique.");
        }

        Product product = new Product();
        product.setSku(command.sku());
        product.setName(command.name());
        product.setUnit(command.unit());
        product.setSellingPrice(command.sellingPrice());
        product.setPurchasePrice(command.purchasePrice());
        product.setStockQty(command.stockQty());
        product.setLowStockThreshold(command.lowStockThreshold());
        product.setStatus(ProductStatus.ACTIVE);

        return productRepositoryPort.save(product);
    }

    public record Command(
            String sku,
            String name,
            String unit,
            BigDecimal sellingPrice,
            BigDecimal purchasePrice,
            BigDecimal stockQty,
            BigDecimal lowStockThreshold
    ) {
    }
}
