package com.something.api.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String unit,
        @DecimalMin("0.0") BigDecimal sellingPrice,
        @DecimalMin("0.0") BigDecimal purchasePrice,
        @DecimalMin("0.0") BigDecimal stockQty,
        @DecimalMin("0.0") BigDecimal lowStockThreshold
) {
}
