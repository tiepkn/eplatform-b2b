package com.eplatform.b2b.common.dto;

public record OrderResponseDto(
    String orderId,
    String status,
    long totalAmountCents,
    String currency,
    String paymentTransactionId,
    String message
) {}
