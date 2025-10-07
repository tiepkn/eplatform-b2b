package com.eplatform.b2b.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderItemDto(
    @NotBlank String sku,
    @Min(1) int quantity,
    @Min(0) long priceCents
) {}
