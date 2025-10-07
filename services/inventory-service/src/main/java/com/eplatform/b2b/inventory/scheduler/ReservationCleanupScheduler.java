package com.eplatform.b2b.inventory.scheduler;

import com.eplatform.b2b.inventory.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCleanupScheduler {
    private final InventoryReservationService reservationService;

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30); // 30 minutes timeout
        log.info("Cleaning up expired reservations before {}", threshold);

        reservationService.cancelExpiredReservations(threshold);
    }
}
