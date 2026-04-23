package com.something.api.controller;

import com.something.api.dto.*;
import com.something.application.usecase.*;
import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetProductUseCase getProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase, 
                            ListProductsUseCase listProductsUseCase,
                            UpdateProductUseCase updateProductUseCase,
                            GetProductUseCase getProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.getProductUseCase = getProductUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiEnvelope<List<ProductResponse>>> listProducts(
            @RequestParam(required = false) ProductStatus status) {
        List<ProductResponse> products = listProductsUseCase.execute(status).stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(ApiEnvelope.success(products));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES')")
    public ResponseEntity<ApiEnvelope<ProductResponse>> getProduct(@PathVariable UUID id) {
        Product product = getProductUseCase.execute(id);
        return ResponseEntity.ok(ApiEnvelope.success(mapToResponse(product)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiEnvelope<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
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
                ApiEnvelope.success(mapToResponse(product))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiEnvelope<ProductResponse>> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        Product product = updateProductUseCase.execute(
                new UpdateProductUseCase.Command(
                        id,
                        request.name(),
                        request.unit(),
                        request.sellingPrice(),
                        request.purchasePrice(),
                        request.stockQty(),
                        request.lowStockThreshold(),
                        null
                )
        );
        return ResponseEntity.ok(ApiEnvelope.success(mapToResponse(product)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiEnvelope<ProductResponse>> updateProductStatus(@PathVariable UUID id, @Valid @RequestBody UpdateProductStatusRequest request) {
        Product product = updateProductUseCase.execute(
                new UpdateProductUseCase.Command(
                        id,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        request.status()
                )
        );
        return ResponseEntity.ok(ApiEnvelope.success(mapToResponse(product)));
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getUnit(),
                product.getSellingPrice(),
                product.getPurchasePrice(),
                product.getStockQty(),
                product.getLowStockThreshold(),
                product.getStatus(),
                product.isLowStock()
        );
    }
}
