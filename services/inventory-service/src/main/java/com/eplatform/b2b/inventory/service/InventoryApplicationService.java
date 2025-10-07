package com.eplatform.b2b.inventory.service;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import com.eplatform.b2b.common.dto.ReserveRequestDto;
import com.eplatform.b2b.common.dto.ReserveResponseDto;
import com.eplatform.b2b.inventory.domain.ProductStock;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryApplicationService {

  private final ProductStockRepository repo;

  public InventoryApplicationService(ProductStockRepository repo) {
    this.repo = repo;
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
      throw new IllegalStateException("Insufficient stock for: " + String.join(",", failed));
    }
    return new ReserveResponseDto(true, List.of());
  }
}
