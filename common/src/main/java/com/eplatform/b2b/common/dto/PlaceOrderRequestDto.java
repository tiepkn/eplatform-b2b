package com.eplatform.b2b.common.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PlaceOrderRequestDto(
    @NotEmpty List<OrderItemDto> items,
    @NotNull String currency
) {}
