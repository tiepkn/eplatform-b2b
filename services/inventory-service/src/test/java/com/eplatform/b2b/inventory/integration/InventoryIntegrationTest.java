package com.eplatform.b2b.inventory.integration;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.inventory.domain.ProductStock;
import com.eplatform.b2b.inventory.domain.Reservation;
import com.eplatform.b2b.inventory.domain.ReservationStatus;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import com.eplatform.b2b.inventory.repo.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
    topics = {"order.placed", "inventory.reserved", "inventory.rejected"})
@Transactional
@DirtiesContext
class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductStockRepository stockRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Setup test data
        ProductStock stock = new ProductStock("INTEGRATION-TEST", 100);
        stockRepository.save(stock);
    }

    @Test
    void completeOrderFlow_ShouldWorkEndToEnd() throws Exception {
        // Given
        ReserveItemDto item = new ReserveItemDto("INTEGRATION-TEST", 5);
        String requestBody = objectMapper.writeValueAsString(List.of(item));

        // When - Place order
        String response = mockMvc.perform(post("/api/v1/inventory/reserve")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then - Verify reservation was created
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);

        // Verify stock was reserved
        ProductStock stock = stockRepository.findBySku("INTEGRATION-TEST").orElseThrow();
        assertThat(stock.getReserved()).isEqualTo(5);
        assertThat(stock.getAvailable()).isEqualTo(95);
    }

    @Test
    void insufficientStock_ShouldReturnError() throws Exception {
        // Given - Try to reserve more than available
        ReserveItemDto item = new ReserveItemDto("INTEGRATION-TEST", 150);
        String requestBody = objectMapper.writeValueAsString(List.of(item));

        // When & Then
        mockMvc.perform(post("/api/v1/inventory/reserve")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
