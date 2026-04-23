package com.something.application.usecase;

import com.something.application.port.out.ProductRepositoryPort;
import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public UpdateProductUseCase(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product execute(Command command) {
        Product product = productRepositoryPort.findById(command.id())
                .orElseThrow(() -> new GetProductUseCase.ProductNotFoundException(command.id()));

        if (command.name() != null) product.setName(command.name());
        if (command.unit() != null) product.setUnit(command.unit());
        if (command.sellingPrice() != null) product.setSellingPrice(command.sellingPrice());
        if (command.purchasePrice() != null) product.setPurchasePrice(command.purchasePrice());
        if (command.stockQty() != null) product.setStockQty(command.stockQty());
        if (command.lowStockThreshold() != null) product.setLowStockThreshold(command.lowStockThreshold());
        if (command.status() != null) product.setStatus(command.status());

        return productRepositoryPort.save(product);
    }

    public record Command(
            UUID id,
            String name,
            String unit,
            BigDecimal sellingPrice,
            BigDecimal purchasePrice,
            BigDecimal stockQty,
            BigDecimal lowStockThreshold,
            ProductStatus status
    ) {
    }
}
