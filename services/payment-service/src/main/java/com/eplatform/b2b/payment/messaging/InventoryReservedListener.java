package com.eplatform.b2b.payment.messaging;

import com.eplatform.b2b.common.events.InventoryReservedEvent;
import com.eplatform.b2b.common.events.InventoryRejectedEvent;
import com.eplatform.b2b.common.events.PaymentFailedEvent;
import com.eplatform.b2b.common.events.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InventoryReservedListener {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public InventoryReservedListener(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = "inventory.reserved")
  public void onInventoryReserved(@Payload InventoryReservedEvent event) {
    try {
      // Demo: always succeed. Here you would integrate payment gateway.
      log.info("Processing payment for order: {}", event.orderId());
      String txId = UUID.randomUUID().toString();
      kafkaTemplate.send("payment.succeeded", event.orderId(), new PaymentSucceededEvent(event.orderId(), txId));
    } catch (Exception ex) {
      log.error("Payment failed for order: " + event.orderId(), ex);
      kafkaTemplate.send("payment.failed", event.orderId(), new PaymentFailedEvent(event.orderId(), ex.getMessage()));
    }
  }

  @KafkaListener(topics = "inventory.rejected")
  public void onInventoryRejected(@Payload InventoryRejectedEvent event) {
    try {
      log.info("Inventory rejected for order: {}", event.orderId());
      kafkaTemplate.send("payment.failed", event.orderId(), new PaymentFailedEvent(event.orderId(), event.reason() != null ? event.reason() : "inventory_rejected"));
    } catch (Exception ex) {
      log.error("Failed to handle inventory rejection for order: " + event.orderId(), ex);
      kafkaTemplate.send("payment.failed", event.orderId(), new PaymentFailedEvent(event.orderId(), ex.getMessage()));
    }
  }
}
