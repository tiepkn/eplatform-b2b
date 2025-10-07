package com.eplatform.b2b.inventory.service;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.common.events.InventoryReservedEvent;
import com.eplatform.b2b.common.events.InventoryRejectedEvent;
import com.eplatform.b2b.inventory.domain.Reservation;
import com.eplatform.b2b.inventory.domain.ReservationItem;
import com.eplatform.b2b.inventory.domain.ReservationStatus;
import com.eplatform.b2b.inventory.exception.InsufficientStockException;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import com.eplatform.b2b.inventory.repo.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationService {
    private final ProductStockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public String createReservation(String orderId, List<ReserveItemDto> items) {
        try {
            log.info("Creating reservation for order: {}", orderId);

            // 1. Check and lock stock for all items
            for (ReserveItemDto item : items) {
                int updated = stockRepository.lockStock(item.sku(), item.quantity());
                if (updated == 0) {
                    // Stock insufficient - release any previously locked stock
                    releasePreviouslyLockedStock(items, item.sku());
                    throw new InsufficientStockException("Insufficient stock for SKU: " + item.sku());
                }
                log.debug("Locked {} units of SKU: {}", item.quantity(), item.sku());
            }

            // 2. Create reservation record
            List<ReservationItem> reservationItems = items.stream()
                .map(i -> new ReservationItem(i.sku(), i.quantity()))
                .collect(Collectors.toList());

            Reservation reservation = new Reservation();
            reservation.setOrderId(orderId);
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setItems(reservationItems);

            reservationRepository.save(reservation);
            log.info("Created reservation for order: {}", orderId);

            // 3. Publish event
            kafkaTemplate.send("inventory.reserved", orderId,
                new InventoryReservedEvent(orderId));

            return orderId;

        } catch (Exception e) {
            log.error("Failed to create reservation for order: " + orderId, e);
            kafkaTemplate.send("inventory.rejected", orderId,
                new InventoryRejectedEvent(orderId, "RESERVATION_FAILED", e.getMessage()));
            throw e;
        }
    }

    @Transactional
    public void confirmReservation(String orderId) {
        log.info("Confirming reservation for order: {}", orderId);
        Reservation reservation = reservationRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + orderId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Invalid reservation status: " + reservation.getStatus());
        }

        // Update stock - move from reserved to unavailable
        for (ReservationItem item : reservation.getItems()) {
            int updated = stockRepository.confirmReservation(item.getSku(), item.getQuantity());
            if (updated == 0) {
                log.error("Failed to confirm reservation for SKU: {} - insufficient reserved stock", item.getSku());
                throw new RuntimeException("Insufficient reserved stock for SKU: " + item.getSku());
            }
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        log.info("Confirmed reservation for order: {}", orderId);
    }

    @Transactional
    public void cancelReservation(String orderId, String reason) {
        log.info("Cancelling reservation for order: {}, reason: {}", orderId, reason);
        Reservation reservation = reservationRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + orderId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.info("Reservation already processed for order: {}, status: {}", orderId, reservation.getStatus());
            return;
        }

        // Release stock back to available
        for (ReservationItem item : reservation.getItems()) {
            int updated = stockRepository.releaseStock(item.getSku(), item.getQuantity());
            if (updated == 0) {
                log.warn("Failed to release stock for SKU: {} - insufficient reserved stock", item.getSku());
            }
        }

        // Update status
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Cancelled reservation for order: {}", orderId);
    }

    @Transactional
    public void cancelExpiredReservations(LocalDateTime threshold) {
        List<Reservation> expired = reservationRepository
            .findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, threshold);

        for (Reservation reservation : expired) {
            try {
                cancelReservation(reservation.getOrderId(), "Reservation expired");
            } catch (Exception ex) {
                log.error("Failed to cancel expired reservation: " + reservation.getOrderId(), ex);
            }
        }
    }

    /**
     * Releases stock that was previously locked in the same transaction
     * Used when a subsequent item fails stock check
     */
    private void releasePreviouslyLockedStock(List<ReserveItemDto> items, String failedSku) {
        // Find items that come before the failed SKU in the list
        boolean shouldRelease = false;
        for (ReserveItemDto item : items) {
            if (shouldRelease) {
                stockRepository.releaseStock(item.sku(), item.quantity());
                log.debug("Released previously locked stock for SKU: {}", item.sku());
            }
            if (item.sku().equals(failedSku)) {
                shouldRelease = true;
            }
        }
    }
}
