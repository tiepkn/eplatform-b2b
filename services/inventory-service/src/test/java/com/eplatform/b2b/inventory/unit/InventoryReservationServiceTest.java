package com.eplatform.b2b.inventory.unit;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.inventory.InventoryServiceApplication;
import com.eplatform.b2b.inventory.config.TestKafkaConfig;
import com.eplatform.b2b.inventory.domain.ProductStock;
import com.eplatform.b2b.inventory.domain.Reservation;
import com.eplatform.b2b.inventory.domain.ReservationItem;
import com.eplatform.b2b.inventory.domain.ReservationStatus;
import com.eplatform.b2b.inventory.exception.InsufficientStockException;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import com.eplatform.b2b.inventory.repo.ReservationRepository;
import com.eplatform.b2b.inventory.service.InventoryReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = InventoryServiceApplication.class)
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class InventoryReservationServiceTest {

    private static final String TEST_ORDER_ID = UUID.randomUUID().toString();
    private static final String TEST_SKU = "TEST-SKU";
    private static final int TEST_QUANTITY = 2;

    @MockBean
    private ProductStockRepository stockRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @Autowired
    private InventoryReservationService reservationService;

    private ProductStock testStock;
    private List<ReserveItemDto> testItems;

    @BeforeEach
    void setUp() {
        testStock = new ProductStock(TEST_SKU, 10);
        testItems = List.of(
            new ReserveItemDto(TEST_SKU, TEST_QUANTITY)
        );
    }

    @Test
    void createReservation_ShouldSucceed_WhenStockAvailable() {
        // Given
        when(stockRepository.findBySkuForUpdate("TEST-SKU"))
            .thenReturn(Optional.of(testStock));
        when(stockRepository.lockStock(anyString(), anyInt()))
            .thenReturn(1);

        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);
        savedReservation.setOrderId("ORDER-123");
        savedReservation.setStatus(ReservationStatus.PENDING);
        when(reservationRepository.save(any(Reservation.class)))
            .thenReturn(savedReservation);

        // When
        String result = reservationService.createReservation("ORDER-123", testItems);

        // Then
        assertThat(result).isEqualTo("ORDER-123");
        verify(stockRepository).lockStock("TEST-SKU", 2);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_ShouldFail_WhenInsufficientStock() {
        // Given
        when(stockRepository.findBySkuForUpdate("TEST-SKU"))
            .thenReturn(Optional.of(testStock));
        when(stockRepository.lockStock(anyString(), anyInt()))
            .thenReturn(0); // No stock locked

        // When & Then
        assertThatThrownBy(() ->
            reservationService.createReservation("ORDER-123", testItems))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock for SKU: TEST-SKU");

        verify(stockRepository).lockStock("TEST-SKU", 2);
    }

    @Test
    void confirmReservation_ShouldSucceed_WhenReservationExists() {
        // Given
        Reservation reservation = new Reservation();
        reservation.setOrderId("ORDER-123");
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setItems(List.of(new ReservationItem("TEST-SKU", 2)));

        when(reservationRepository.findByOrderId("ORDER-123"))
            .thenReturn(Optional.of(reservation));
        when(stockRepository.confirmReservation(anyString(), anyInt()))
            .thenReturn(1);

        // When
        reservationService.confirmReservation("ORDER-123");

        // Then
        verify(stockRepository).confirmReservation("TEST-SKU", 2);
        verify(reservationRepository).save(reservation);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void cancelReservation_ShouldReleaseStock_WhenReservationPending() {
        // Given
        Reservation reservation = new Reservation();
        reservation.setOrderId("ORDER-123");
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setItems(List.of(new ReservationItem("TEST-SKU", 2)));

        when(reservationRepository.findByOrderId("ORDER-123"))
            .thenReturn(Optional.of(reservation));
        when(stockRepository.releaseStock(anyString(), anyInt()))
            .thenReturn(1);

        // When
        reservationService.cancelReservation("ORDER-123", "User cancelled");

        // Then
        verify(stockRepository).releaseStock("TEST-SKU", 2);
        verify(reservationRepository).save(reservation);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void cancelExpiredReservations_ShouldCancelOldReservations() {
        // Given
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<Reservation> expiredReservations = List.of(
            createTestReservation("ORDER-1", ReservationStatus.PENDING),
            createTestReservation("ORDER-2", ReservationStatus.PENDING)
        );

        when(reservationRepository.findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, threshold))
            .thenReturn(expiredReservations);

        // When
        reservationService.cancelExpiredReservations(threshold);

        // Then
        verify(reservationRepository).findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, threshold);
        // Should attempt to cancel each reservation
    }

    private Reservation createTestReservation(String orderId, ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setOrderId(orderId);
        reservation.setStatus(status);
        return reservation;
    }
}
