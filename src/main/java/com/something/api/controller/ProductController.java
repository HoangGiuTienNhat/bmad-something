package com.something.api.controller;

import com.something.api.dto.CreateProductRequest;
import com.something.api.dto.CreateProductResponse;
import com.something.application.usecase.CreateProductUseCase;
import com.something.domain.entity.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = createProductUseCase.execute(
                new CreateProductUseCase.Command(
                        request.sku(),
                        request.name(),
                        request.unit(),
                        request.sellingPrice(),
                        request.purchasePrice(),
                        request.stockQty(),
                        request.lowStockThreshold()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CreateProductResponse(
                        product.getId(),
                        product.getSku(),
                        product.getName(),
                        product.getUnit(),
                        product.getSellingPrice(),
                        product.getPurchasePrice(),
                        product.getStockQty(),
                        product.getLowStockThreshold(),
                        product.getStatus()
                )
        );
    }
}
