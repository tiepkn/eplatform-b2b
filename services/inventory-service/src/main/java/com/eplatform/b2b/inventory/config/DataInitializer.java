package com.eplatform.b2b.inventory.config;

import com.eplatform.b2b.inventory.domain.ProductStock;
import com.eplatform.b2b.inventory.repo.ProductStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

  @Bean
  CommandLineRunner initStock(ProductStockRepository repo) {
    return args -> {
      if (repo.count() == 0) {
        repo.save(new ProductStock("SKU-001", 100));
        repo.save(new ProductStock("SKU-002", 50));
        repo.save(new ProductStock("SKU-003", 200));
      }
    };
  }
}
