// package com.eplatform.b2b.order.unit;

// import com.eplatform.b2b.common.dto.OrderItemDto;
// import com.eplatform.b2b.common.dto.OrderResponseDto;
// import com.eplatform.b2b.common.dto.PlaceOrderRequestDto;
// import com.eplatform.b2b.order.domain.Order;
// import com.eplatform.b2b.order.domain.OrderStatus;
// import com.eplatform.b2b.order.repo.OrderRepository;
// import com.eplatform.b2b.order.service.OrderPlacementService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.math.BigDecimal;
// import java.util.List;

// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class OrderPlacementServiceTest {

//     @Mock
//     private OrderRepository orderRepository;

//     @Mock
//     private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

//     @InjectMocks
//     private OrderPlacementService orderPlacementService;

//     private PlaceOrderRequestDto validRequest;

//     @BeforeEach
//     void setUp() {
//         List<OrderItemDto> items = List.of(
//             new OrderItemDto("PRODUCT-1", 2, 1000L),
//             new OrderItemDto("PRODUCT-2", 1, 2000L)
//         );
//         validRequest = new PlaceOrderRequestDto(items, "USD");
//     }

//     @Test
//     void placeOrder_ShouldSucceed_WithValidRequest() {
//         // Given
//         Order savedOrder = new Order();
//         savedOrder.setId(1L);
//         savedOrder.setOrderNumber("ORD-2024-001");
//         savedOrder.setStatus(OrderStatus.PENDING);
//         savedOrder.setTotalAmount(new BigDecimal("40.00"));

//         when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

//         // When
//         OrderResponseDto response = orderPlacementService.placeOrder(validRequest);

//         // Then
//         assertThat(response).isNotNull();
//         assertThat(response.orderNumber()).isEqualTo("ORD-2024-001");
//         assertThat(response.status()).isEqualTo(OrderStatus.PENDING);

//         verify(orderRepository).save(any(Order.class));
//         verify(kafkaTemplate).send(eq("order.placed"), anyString(), any());
//     }

//     @Test
//     void placeOrder_ShouldCalculateTotalCorrectly() {
//         // When
//         OrderResponseDto response = orderPlacementService.placeOrder(validRequest);

//         // Then - (2 * 10.00) + (1 * 20.00) = 40.00
//         assertThat(response.totalAmount()).isEqualTo(new BigDecimal("40.00"));
//     }

//     @Test
//     void placeOrderAsync_ShouldReturnAccepted() {
//         // Given
//         Order savedOrder = new Order();
//         savedOrder.setId(1L);
//         savedOrder.setOrderNumber("ORD-2024-002");

//         when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

//         // When
//         OrderResponseDto response = orderPlacementService.placeOrderAsync(validRequest);

//         // Then
//         assertThat(response).isNotNull();
//         assertThat(response.orderNumber()).isEqualTo("ORD-2024-002");
//         verify(kafkaTemplate).send(eq("order.placed"), anyString(), any());
//     }
// }
