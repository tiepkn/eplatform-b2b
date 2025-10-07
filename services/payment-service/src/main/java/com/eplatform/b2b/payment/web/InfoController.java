package com.eplatform.b2b.payment.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
  @GetMapping("/api/v1/info")
  public String info() {
    return "payment-service";
  }
}
