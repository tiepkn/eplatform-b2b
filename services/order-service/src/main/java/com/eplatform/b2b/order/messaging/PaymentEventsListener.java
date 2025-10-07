package com.eplatform.b2b.order.messaging;

import com.eplatform.b2b.common.events.PaymentFailedEvent;
import com.eplatform.b2b.common.events.PaymentSucceededEvent;
import com.eplatform.b2b.order.domain.Order;
import com.eplatform.b2b.order.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PaymentEventsListener {

  private final OrderRepository orders;

  public PaymentEventsListener(OrderRepository orders) {
    this.orders = orders;
  }

  @KafkaListener(topics = "payment.succeeded")
  @Transactional
  public void onPaymentSucceeded(@Payload PaymentSucceededEvent event) {
    try {
      log.info("Payment succeeded for order: {}", event.orderId());
      orders.findById(event.orderId()).ifPresent(order -> {
        order.setStatus("PAID");
        order.setPaymentTransactionId(event.transactionId());
        orders.save(order);
        log.info("Updated order {} status to PAID", event.orderId());
      });
    } catch (Exception ex) {
      log.error("Failed to update order status for payment success: " + event.orderId(), ex);
    }
  }

  @KafkaListener(topics = "payment.failed")
  @Transactional
  public void onPaymentFailed(@Payload PaymentFailedEvent event) {
    try {
      log.info("Payment failed for order: {}", event.orderId());
      orders.findById(event.orderId()).ifPresent(order -> {
        order.setStatus("FAILED");
        orders.save(order);
        log.info("Updated order {} status to FAILED", event.orderId());
      });
    } catch (Exception ex) {
      log.error("Failed to update order status for payment failure: " + event.orderId(), ex);
    }
  }
}
