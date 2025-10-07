package com.eplatform.b2b.inventory.repo;

import com.eplatform.b2b.inventory.domain.Reservation;
import com.eplatform.b2b.inventory.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByOrderId(String orderId);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'CONFIRMED' WHERE r.orderId = :orderId")
    int confirmReservation(@Param("orderId") String orderId);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'CANCELLED' WHERE r.orderId = :orderId")
    int cancelReservation(@Param("orderId") String orderId);

    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime threshold);
}
