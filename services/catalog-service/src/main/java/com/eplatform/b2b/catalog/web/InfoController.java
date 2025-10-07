package com.eplatform.b2b.catalog.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
  @GetMapping("/api/v1/info")
  public String info() {
    return "catalog-service";
  }
}
