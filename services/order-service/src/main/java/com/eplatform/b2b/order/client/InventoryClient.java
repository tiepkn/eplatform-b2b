package com.eplatform.b2b.order.client;

import com.eplatform.b2b.common.dto.ReserveRequestDto;
import com.eplatform.b2b.common.dto.ReserveResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

  @PostMapping("/api/v1/inventory/reserve")
  ReserveResponseDto reserve(@RequestBody ReserveRequestDto request);
}
