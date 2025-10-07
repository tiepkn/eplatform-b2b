package com.eplatform.b2b.inventory.messaging;

import com.eplatform.b2b.common.dto.ReserveRequestDto;
import com.eplatform.b2b.common.events.OrderPlacedEvent;
import com.eplatform.b2b.inventory.service.InventoryApplicationService;
import com.eplatform.b2b.inventory.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPlacedListener {

  private final InventoryApplicationService service;
  private final InventoryReservationService reservationService;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @KafkaListener(topics = "order.placed")
  public void onOrderPlaced(@Payload OrderPlacedEvent event) {
    try {
      log.info("Received order.placed: {}", event.orderId());
      String result = reservationService.createReservation(event.orderId(), event.items());
      log.debug("Reservation created for order: {}", result);
    } catch (Exception ex) {
      log.error("Failed to process order.placed: " + event.orderId(), ex);
      kafkaTemplate.send("inventory.rejected", event.orderId(),
          new com.eplatform.b2b.common.events.InventoryRejectedEvent(event.orderId(),
              "PROCESSING_ERROR", ex.getMessage()));
    }
  }
}
