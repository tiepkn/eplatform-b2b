package com.eplatform.b2b.common.events;

public record PaymentSucceededEvent(
    String orderId,
    String transactionId
) {}
