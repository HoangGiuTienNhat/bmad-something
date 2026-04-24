package com.something.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.something.application.port.out.ProductRepositoryPort;
import com.something.application.usecase.CreateProductUseCase;
import com.something.application.usecase.UpdateProductUseCase;
import com.something.application.usecase.GetProductUseCase;
import com.something.domain.entity.Product;
import com.something.domain.entity.ProductStatus;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepositoryPort productRepositoryPort;

    private CreateProductUseCase createProductUseCase;
    private UpdateProductUseCase updateProductUseCase;

    @BeforeEach
    void setUp() {
        createProductUseCase = new CreateProductUseCase(productRepositoryPort);
        updateProductUseCase = new UpdateProductUseCase(productRepositoryPort);
    }

    @Test
    void shouldUpdateProductFieldsWhenExists() {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Old Name");

        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
                id, "New Name", null, null, null, null, null, null
        );

        when(productRepositoryPort.findById(id)).thenReturn(Optional.of(product));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(product);

        Product result = updateProductUseCase.execute(command);

        assertEquals("New Name", result.getName());
        verify(productRepositoryPort).save(product);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistentProduct() {
        UUID id = UUID.randomUUID();
        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
                id, "New Name", null, null, null, null, null, null
        );

        when(productRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(GetProductUseCase.ProductNotFoundException.class, () -> updateProductUseCase.execute(command));
    }

    @Test
    void shouldRejectWhenSkuAlreadyExists() {
        CreateProductUseCase.Command command = sampleCommand();
        when(productRepositoryPort.existsBySku(command.sku())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createProductUseCase.execute(command)
        );

        assertEquals("SKU must be unique.", exception.getMessage());
        verify(productRepositoryPort, never()).save(any(Product.class));
    }

    @Test
    void shouldCreateProductWithActiveStatusWhenSkuIsUnique() {
        CreateProductUseCase.Command command = sampleCommand();
        when(productRepositoryPort.existsBySku(command.sku())).thenReturn(false);
        when(productRepositoryPort.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(UUID.randomUUID());
            return product;
        });

        Product result = createProductUseCase.execute(command);

        assertEquals(ProductStatus.ACTIVE, result.getStatus());
        assertEquals(command.sku(), result.getSku());
        verify(productRepositoryPort).save(any(Product.class));
    }

    private CreateProductUseCase.Command sampleCommand() {
        return new CreateProductUseCase.Command(
                "SKU-001",
                "Sample Product",
                "PCS",
                new BigDecimal("100.00"),
                new BigDecimal("70.00"),
                new BigDecimal("10.000"),
                new BigDecimal("3.000")
        );
    }
}
