package com.something.domain.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void shouldBeLowStockWhenQtyIsLessOrEqualToThreshold() {
        Product product = new Product();
        product.setStockQty(new BigDecimal("5.000"));
        product.setLowStockThreshold(new BigDecimal("5.000"));
        assertTrue(product.isLowStock());

        product.setStockQty(new BigDecimal("4.000"));
        assertTrue(product.isLowStock());
    }

    @Test
    void shouldNotBeLowStockWhenQtyIsGreaterThanThreshold() {
        Product product = new Product();
        product.setStockQty(new BigDecimal("6.000"));
        product.setLowStockThreshold(new BigDecimal("5.000"));
        assertFalse(product.isLowStock());
    }

    @Test
    void shouldNotBeLowStockWhenQtyOrThresholdIsNull() {
        Product product = new Product();
        assertFalse(product.isLowStock());

        product.setStockQty(new BigDecimal("5.000"));
        assertFalse(product.isLowStock());

        product.setStockQty(null);
        product.setLowStockThreshold(new BigDecimal("5.000"));
        assertFalse(product.isLowStock());
    }
}
