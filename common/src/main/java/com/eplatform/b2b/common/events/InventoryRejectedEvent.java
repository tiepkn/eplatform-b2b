package com.eplatform.b2b.common.events;

public record InventoryRejectedEvent(
  String orderId,
  String failedSkus,
  String reason
) {}
