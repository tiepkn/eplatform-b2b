package com.eplatform.b2b.common.dto;

public record PaymentChargeResponseDto(
    boolean success,
    String transactionId,
    String message
) {}
