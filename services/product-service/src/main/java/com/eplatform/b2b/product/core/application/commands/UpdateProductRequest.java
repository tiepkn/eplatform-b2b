package com.eplatform.b2b.product.core.application.commands;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing product.
 */
public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        String category,
        Boolean active
) {}
