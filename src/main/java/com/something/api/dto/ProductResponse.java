package com.something.api.dto;

import com.something.domain.entity.ProductStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String unit,
        BigDecimal sellingPrice,
        BigDecimal purchasePrice,
        BigDecimal stockQty,
        BigDecimal lowStockThreshold,
        ProductStatus status
) {
}
