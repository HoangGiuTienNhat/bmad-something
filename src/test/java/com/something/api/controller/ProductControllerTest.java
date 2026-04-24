package com.something.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.something.application.usecase.CreateProductUseCase;
import com.something.application.usecase.ListProductsUseCase;
import com.something.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductController.class)
@Import({SecurityConfig.class, com.something.api.exception.GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private ListProductsUseCase listProductsUseCase;

    @MockBean
    private com.something.application.usecase.UpdateProductUseCase updateProductUseCase;

    @MockBean
    private com.something.application.usecase.GetProductUseCase getProductUseCase;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenSkuIsDuplicate() throws Exception {
        when(createProductUseCase.execute(any(CreateProductUseCase.Command.class)))
                .thenThrow(new IllegalArgumentException("SKU must be unique."));

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "sku":"DUP-001",
                                  "name":"Duplicate Product",
                                  "unit":"PCS",
                                  "sellingPrice":100.00,
                                  "purchasePrice":70.00,
                                  "stockQty":10.000,
                                  "lowStockThreshold":3.000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("SKU must be unique."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToUpdateProduct() throws Exception {
        java.util.UUID id = java.util.UUID.randomUUID();
        com.something.domain.entity.Product product = new com.something.domain.entity.Product();
        product.setId(id);
        product.setSku("SKU-001");
        product.setName("Updated Name");
        product.setUnit("PCS");
        product.setSellingPrice(new java.math.BigDecimal("120.00"));
        product.setPurchasePrice(new java.math.BigDecimal("80.00"));
        product.setStockQty(new java.math.BigDecimal("10.000"));
        product.setLowStockThreshold(new java.math.BigDecimal("3.000"));
        product.setStatus(com.something.domain.entity.ProductStatus.ACTIVE);

        when(updateProductUseCase.execute(any(com.something.application.usecase.UpdateProductUseCase.Command.class))).thenReturn(product);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/products/" + id)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name":"Updated Name",
                                  "unit":"PCS",
                                  "sellingPrice":120.00,
                                  "purchasePrice":80.00,
                                  "stockQty":10.000,
                                  "lowStockThreshold":3.000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.sellingPrice").value(120.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToDeactivateProduct() throws Exception {
        java.util.UUID id = java.util.UUID.randomUUID();
        com.something.domain.entity.Product product = new com.something.domain.entity.Product();
        product.setId(id);
        product.setSku("SKU-001");
        product.setStatus(com.something.domain.entity.ProductStatus.INACTIVE);

        when(updateProductUseCase.execute(any(com.something.application.usecase.UpdateProductUseCase.Command.class))).thenReturn(product);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/products/" + id + "/status")
                        .contentType("application/json")
                        .content("""
                                {
                                  "status":"INACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldExposeLowStockStatus() throws Exception {
        java.util.UUID id = java.util.UUID.randomUUID();
        com.something.domain.entity.Product product = new com.something.domain.entity.Product();
        product.setId(id);
        product.setSku("LOW-001");
        product.setStockQty(new java.math.BigDecimal("2.000"));
        product.setLowStockThreshold(new java.math.BigDecimal("5.000"));
        product.setStatus(com.something.domain.entity.ProductStatus.ACTIVE);

        when(getProductUseCase.execute(id)).thenReturn(product);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLowStock").value(true));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void shouldDenySalesStaffFromUpdatingProduct() throws Exception {
        java.util.UUID id = java.util.UUID.randomUUID();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/products/" + id)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name":"Updated Name"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "sku":"",
                                  "name":"Invalid Product",
                                  "unit":"PCS",
                                  "sellingPrice":-10.00,
                                  "purchasePrice":70.00,
                                  "stockQty":10.000,
                                  "lowStockThreshold":3.000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }
}
