package com.eplatform.b2b.inventory.messaging;

import com.eplatform.b2b.common.events.PaymentFailedEvent;
import com.eplatform.b2b.common.events.PaymentSucceededEvent;
import com.eplatform.b2b.inventory.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventsListener {
    private final InventoryReservationService reservationService;

    @KafkaListener(topics = "payment.succeeded")
    public void onPaymentSucceeded(@Payload PaymentSucceededEvent event) {
        try {
            log.info("Processing payment.succeeded for order: {}", event.orderId());
            reservationService.confirmReservation(event.orderId());
        } catch (Exception ex) {
            log.error("Failed to confirm reservation: " + event.orderId(), ex);
            // Consider sending to a dead-letter queue for manual intervention
        }
    }

    @KafkaListener(topics = "payment.failed")
    public void onPaymentFailed(@Payload PaymentFailedEvent event) {
        try {
            log.info("Processing payment.failed for order: {}", event.orderId());
            reservationService.cancelReservation(event.orderId(),
                "Payment failed: " + event.reason());
        } catch (Exception ex) {
            log.error("Failed to cancel reservation: " + event.orderId(), ex);
        }
    }
}
