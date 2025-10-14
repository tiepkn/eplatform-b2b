package com.eplatform.b2b.product.core.application.commands;

/**
 * DTO for product data transfer between layers.
 * Used for API communication and doesn't expose domain internals.
 */
public record ProductDto(
        Long id,
        String sku,
        String name,
        String description,
        java.math.BigDecimal price,
        String category,
        Long supplierId,
        String supplierSku,
        java.math.BigDecimal supplierPrice,
        boolean active,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
) {}
