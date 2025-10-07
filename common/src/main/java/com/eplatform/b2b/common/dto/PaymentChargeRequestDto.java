package com.eplatform.b2b.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaymentChargeRequestDto(
    @NotBlank String orderId,
    @Min(0) long amountCents,
    @NotBlank String currency
) {}
