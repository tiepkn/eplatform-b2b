package com.eplatform.b2b.order.service;

import com.eplatform.b2b.common.dto.*;
import com.eplatform.b2b.order.client.InventoryClient;
import com.eplatform.b2b.order.client.PaymentClient;
import com.eplatform.b2b.order.messaging.OrderEventsProducer;
import com.eplatform.b2b.order.domain.Order;
import com.eplatform.b2b.order.domain.OrderItem;
import com.eplatform.b2b.order.repo.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPlacementService {

  private final OrderRepository orders;
  private final InventoryClient inventoryClient;
  private final PaymentClient paymentClient;
  private final OrderEventsProducer events;

  public OrderPlacementService(OrderRepository orders, InventoryClient inventoryClient, PaymentClient paymentClient, OrderEventsProducer events) {
    this.orders = orders;
    this.inventoryClient = inventoryClient;
    this.paymentClient = paymentClient;
    this.events = events;
  }

  @Transactional
  public OrderResponseDto placeOrder(PlaceOrderRequestDto req) {
    // 1) Create Order aggregate
    Order order = new Order();
    order.setCurrency(req.currency());
    order.setStatus("CREATED");
    long total = 0L;
    for (OrderItemDto i : req.items()) {
      OrderItem oi = new OrderItem();
      oi.setSku(i.sku());
      oi.setQuantity(i.quantity());
      oi.setPriceCents(i.priceCents());
      order.addItem(oi);
      total += (long) i.quantity() * i.priceCents();
    }
    order.setTotalAmountCents(total);
    orders.save(order);

    // 2) Reserve inventory
    List<ReserveItemDto> reserveItems = req.items().stream()
        .map(i -> new ReserveItemDto(i.sku(), i.quantity()))
        .collect(Collectors.toList());
    ReserveResponseDto reserve = inventoryClient.reserve(new ReserveRequestDto(reserveItems));
    if (!reserve.success()) {
      order.setStatus("FAILED");
      orders.save(order);
      return new OrderResponseDto(order.getId(), order.getStatus(), order.getTotalAmountCents(), order.getCurrency(), null,
          "Inventory reservation failed for: " + String.join(",", reserve.failedSkus()));
    }

    order.setStatus("RESERVED");
    orders.save(order);

    // 3) Charge payment
    PaymentChargeResponseDto charge = paymentClient.charge(new PaymentChargeRequestDto(order.getId(), order.getTotalAmountCents(), order.getCurrency()));
    if (charge.success()) {
      order.setStatus("PAID");
      order.setPaymentTransactionId(charge.transactionId());
      orders.save(order);
      return new OrderResponseDto(order.getId(), order.getStatus(), order.getTotalAmountCents(), order.getCurrency(), order.getPaymentTransactionId(), "OK");
    } else {
      order.setStatus("FAILED");
      orders.save(order);
      return new OrderResponseDto(order.getId(), order.getStatus(), order.getTotalAmountCents(), order.getCurrency(), null,
          charge.message() != null ? charge.message() : "Payment failed");
    }
  }

  @Transactional
  public OrderResponseDto placeOrderAsync(PlaceOrderRequestDto req) {
    // Create order and publish event; downstream services will progress status
    Order order = new Order();
    order.setCurrency(req.currency());
    order.setStatus("CREATED");
    long total = 0L;
    for (OrderItemDto i : req.items()) {
      OrderItem oi = new OrderItem();
      oi.setSku(i.sku());
      oi.setQuantity(i.quantity());
      oi.setPriceCents(i.priceCents());
      order.addItem(oi);
      total += (long) i.quantity() * i.priceCents();
    }
    order.setTotalAmountCents(total);
    orders.save(order);

    // Publish event to Kafka for inventory → payment pipeline
    events.publishOrderPlaced(order.getId(), req, total);

    return new OrderResponseDto(order.getId(), order.getStatus(), order.getTotalAmountCents(), order.getCurrency(), null, "ORDER_PLACED_EVENT_PUBLISHED");
  }
}
