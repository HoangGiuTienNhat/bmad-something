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
