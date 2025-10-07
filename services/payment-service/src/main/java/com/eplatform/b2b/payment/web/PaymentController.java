package com.eplatform.b2b.payment.web;

import com.eplatform.b2b.common.dto.PaymentChargeRequestDto;
import com.eplatform.b2b.common.dto.PaymentChargeResponseDto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

  @PostMapping("/charge")
  public ResponseEntity<PaymentChargeResponseDto> charge(@Valid @RequestBody PaymentChargeRequestDto req) {
    // Demo: always succeed
    String txId = UUID.randomUUID().toString();
    return ResponseEntity.ok(new PaymentChargeResponseDto(true, txId, "charged"));
  }
}
