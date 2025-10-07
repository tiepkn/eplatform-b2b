package com.eplatform.b2b.common.events;

public record PaymentFailedEvent(
    String orderId,
    String reason
) {}
