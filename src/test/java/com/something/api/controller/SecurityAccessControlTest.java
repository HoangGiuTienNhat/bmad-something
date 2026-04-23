package com.something.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.something.application.usecase.CreateProductUseCase;
import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import com.something.infrastructure.security.SecurityConfig;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductController.class)
@Import(SecurityConfig.class)
class SecurityAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private com.something.application.usecase.ListProductsUseCase listProductsUseCase;

    @MockBean
    private com.something.application.usecase.UpdateProductUseCase updateProductUseCase;

    @MockBean
    private com.something.application.usecase.GetProductUseCase getProductUseCase;

    @Test
    void shouldRejectAnonymousRequestToProtectedEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToCreateProduct() throws Exception {
        when(createProductUseCase.execute(any(CreateProductUseCase.Command.class))).thenReturn(sampleProduct());

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "sku":"SKU-SEC-001",
                                  "name":"Security Product",
                                  "unit":"PCS",
                                  "sellingPrice":100.00,
                                  "purchasePrice":70.00,
                                  "stockQty":10.000,
                                  "lowStockThreshold":3.000
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void shouldDenySalesStaffWhenCreatingProduct() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "sku":"SKU-SEC-002",
                                  "name":"Denied Product",
                                  "unit":"PCS",
                                  "sellingPrice":100.00,
                                  "purchasePrice":70.00,
                                  "stockQty":10.000,
                                  "lowStockThreshold":3.000
                                }
                                """))
                .andExpect(status().isForbidden());

        verify(createProductUseCase, never()).execute(any(CreateProductUseCase.Command.class));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void shouldDenySalesStaffOnAdminOnlyReportingEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/reports/revenue-daily"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void shouldAllowSalesStaffToViewProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void shouldAllowSalesStaffAccessToSalesArea() throws Exception {
        mockMvc.perform(get("/api/v1/sales-orders/test"))
                .andExpect(status().isNotFound());
    }

    private Product sampleProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setSku("SKU-SEC-001");
        product.setName("Security Product");
        product.setUnit("PCS");
        product.setSellingPrice(new BigDecimal("100.00"));
        product.setPurchasePrice(new BigDecimal("70.00"));
        product.setStockQty(new BigDecimal("10.000"));
        product.setLowStockThreshold(new BigDecimal("3.000"));
        product.setStatus(ProductStatus.ACTIVE);
        return product;
    }
}
