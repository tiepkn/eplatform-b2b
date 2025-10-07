package com.eplatform.b2b.inventory.web;

import com.eplatform.b2b.common.dto.ReserveRequestDto;
import com.eplatform.b2b.common.dto.ReserveResponseDto;
import com.eplatform.b2b.inventory.service.InventoryApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
  private final InventoryApplicationService service;

  public InventoryController(InventoryApplicationService service) {
    this.service = service;
  }

  @PostMapping("/reserve")
  public ResponseEntity<ReserveResponseDto> reserve(@Valid @RequestBody ReserveRequestDto request) {
    return ResponseEntity.ok(service.reserve(request));
  }
}
