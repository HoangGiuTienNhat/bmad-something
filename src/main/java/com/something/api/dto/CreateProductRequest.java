package com.something.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotBlank String unit,
        @NotNull @DecimalMin("0.0") BigDecimal sellingPrice,
        @NotNull @DecimalMin("0.0") BigDecimal purchasePrice,
        @NotNull @DecimalMin("0.0") BigDecimal stockQty,
        @NotNull @DecimalMin("0.0") BigDecimal lowStockThreshold
) {
}
