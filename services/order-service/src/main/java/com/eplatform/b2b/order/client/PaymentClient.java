package com.eplatform.b2b.order.client;

import com.eplatform.b2b.common.dto.PaymentChargeRequestDto;
import com.eplatform.b2b.common.dto.PaymentChargeResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {

  @PostMapping("/api/v1/payments/charge")
  PaymentChargeResponseDto charge(@RequestBody PaymentChargeRequestDto request);
}
