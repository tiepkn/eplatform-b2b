package com.eplatform.b2b.product.core.application.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Request DTO for creating a product with supplier information.
 */
public record CreateProductWithSupplierRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        @NotBlank String category,
        @NotNull Long supplierId,
        @NotBlank String supplierSku,
        @NotNull @Positive BigDecimal supplierPrice
) {}
