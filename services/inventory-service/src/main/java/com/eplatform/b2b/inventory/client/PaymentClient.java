package com.eplatform.b2b.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service")
public interface PaymentClient {
  @GetMapping("/api/v1/info")
  String info();
}
