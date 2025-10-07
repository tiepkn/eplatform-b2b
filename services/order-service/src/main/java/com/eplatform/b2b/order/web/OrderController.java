package com.eplatform.b2b.order.web;

import com.eplatform.b2b.common.dto.OrderResponseDto;
import com.eplatform.b2b.common.dto.PlaceOrderRequestDto;
import com.eplatform.b2b.order.service.OrderPlacementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderPlacementService service;

  public OrderController(OrderPlacementService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<OrderResponseDto> place(@Valid @RequestBody PlaceOrderRequestDto request) {
    return ResponseEntity.ok(service.placeOrder(request));
  }

  @PostMapping("/async")
  public ResponseEntity<OrderResponseDto> placeAsync(@Valid @RequestBody PlaceOrderRequestDto request) {
    return ResponseEntity.accepted().body(service.placeOrderAsync(request));
  }
}
