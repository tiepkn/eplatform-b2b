package com.eplatform.b2b.inventory.integration;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.common.dto.ReserveRequestDto;
import com.eplatform.b2b.inventory.InventoryServiceApplication;
import com.eplatform.b2b.inventory.config.TestKafkaConfig;
import com.eplatform.b2b.inventory.domain.ProductStock;
import com.eplatform.b2b.inventory.domain.Reservation;
import com.eplatform.b2b.inventory.domain.ReservationStatus;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import com.eplatform.b2b.inventory.repo.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = InventoryServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class InventoryIntegrationTest {

    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductStock testStock;

    @BeforeEach
    void setUp() {
        // Clear existing data
        reservationRepository.deleteAll();
        productStockRepository.deleteAll();

        // Setup test data
        testStock = new ProductStock("TEST-SKU", 10);
        productStockRepository.save(testStock);
    }

    @Test
    void shouldReserveInventorySuccessfully() throws Exception {
        // Given
        List<ReserveItemDto> items = List.of(new ReserveItemDto("TEST-SKU", 2));
        ReserveRequestDto request = new ReserveRequestDto(items);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/inventory/reserve",
            HttpMethod.POST,
            new HttpEntity<>(request, createJsonHeaders()),
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify stock was reduced
        ProductStock updatedStock = productStockRepository.findBySku("TEST-SKU").orElseThrow();
        assertThat(updatedStock.getAvailable()).isEqualTo(8);

        // Verify reservation was created
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void shouldRejectReservationWhenInsufficientStock() throws Exception {
        // Given - stock has only 10 items, but we try to reserve 15
        List<ReserveItemDto> items = List.of(new ReserveItemDto("TEST-SKU", 15));
        ReserveRequestDto request = new ReserveRequestDto(items);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/inventory/reserve",
            HttpMethod.POST,
            new HttpEntity<>(request, createJsonHeaders()),
            String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Verify stock was not reduced
        ProductStock unchangedStock = productStockRepository.findBySku("TEST-SKU").orElseThrow();
        assertThat(unchangedStock.getAvailable()).isEqualTo(10);

        // Verify no reservation was created
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isEmpty();
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
