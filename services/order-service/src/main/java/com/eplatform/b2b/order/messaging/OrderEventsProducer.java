package com.eplatform.b2b.order.messaging;

import com.eplatform.b2b.common.dto.OrderItemDto;
import com.eplatform.b2b.common.dto.PlaceOrderRequestDto;
import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.common.events.OrderPlacedEvent;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public OrderEventsProducer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publishOrderPlaced(String orderId, PlaceOrderRequestDto req, long totalCents) {
    List<ReserveItemDto> items = req.items().stream()
        .map(i -> new ReserveItemDto(i.sku(), i.quantity()))
        .collect(Collectors.toList());
    OrderPlacedEvent evt = new OrderPlacedEvent(orderId, items, totalCents, req.currency());
    kafkaTemplate.send("order.placed", orderId, evt);
  }
}
