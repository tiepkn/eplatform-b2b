package com.eplatform.b2b.inventory.service;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.common.dto.ReserveRequestDto;
import com.eplatform.b2b.common.dto.ReserveResponseDto;
import com.eplatform.b2b.inventory.domain.ProductStock;
import com.eplatform.b2b.inventory.domain.Reservation;
import com.eplatform.b2b.inventory.domain.ReservationItem;
import com.eplatform.b2b.inventory.domain.ReservationStatus;
import com.eplatform.b2b.inventory.exception.InsufficientStockException;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import com.eplatform.b2b.inventory.repo.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryApplicationService {

  private final ProductStockRepository repo;
  private final ReservationRepository reservationRepository;

  public InventoryApplicationService(ProductStockRepository repo, ReservationRepository reservationRepository) {
    this.repo = repo;
    this.reservationRepository = reservationRepository;
  }

  @Transactional
  public ReserveResponseDto reserve(ReserveRequestDto request) {
    // Ensure all-or-nothing: if any SKU fails, throw to rollback the transaction
    List<String> failed = new ArrayList<>();
    for (ReserveItemDto item : request.items()) {
      // Ensure SKU row exists; create with 0 if missing
      repo.findBySku(item.sku()).orElseGet(() -> repo.save(new ProductStock(item.sku(), 0)));
      int updated = repo.tryReserve(item.sku(), item.quantity());
      if (updated != 1) {
        failed.add(item.sku());
      }
    }
    if (!failed.isEmpty()) {
      throw new InsufficientStockException("Insufficient stock for: " + String.join(",", failed));
    }

    // Create a reservation entry with CONFIRMED status for the reserved items
    Reservation reservation = new Reservation();
    // For integration tests that don't pass an orderId, we can synthesize one
    reservation.setOrderId("RES-" + System.currentTimeMillis());
    reservation.setStatus(ReservationStatus.CONFIRMED);
    List<ReservationItem> items = request.items().stream()
        .map(i -> new ReservationItem(i.sku(), i.quantity()))
        .collect(Collectors.toList());
    reservation.setItems(items);
    reservationRepository.save(reservation);

    return new ReserveResponseDto(true, List.of());
  }
}
