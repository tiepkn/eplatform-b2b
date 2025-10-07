package com.eplatform.b2b.inventory.repo;

import com.eplatform.b2b.inventory.domain.ProductStock;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

  Optional<ProductStock> findBySku(String sku);

  @Modifying
  @Transactional
  @Query("update ProductStock p set p.available = p.available - :qty where p.sku = :sku and p.available >= :qty")
  int tryReserve(@Param("sku") String sku, @Param("qty") int qty);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM ProductStock p WHERE p.sku = :sku")
  Optional<ProductStock> findBySkuForUpdate(@Param("sku") String sku);

  // New methods for reservation pattern
  @Modifying
  @Query("UPDATE ProductStock p SET p.available = p.available - :qty, " +
         "p.reserved = p.reserved + :qty " +
         "WHERE p.sku = :sku AND p.available >= :qty")
  int lockStock(@Param("sku") String sku, @Param("qty") int qty);

  @Modifying
  @Query("UPDATE ProductStock p SET p.reserved = p.reserved - :qty " +
         "WHERE p.sku = :sku AND p.reserved >= :qty")
  int confirmReservation(@Param("sku") String sku, @Param("qty") int qty);
  @Modifying
  @Query("UPDATE ProductStock p SET p.available = p.available + :qty, " +
         "p.reserved = p.reserved - :qty " +
         "WHERE p.sku = :sku AND p.reserved >= :qty")
  int releaseStock(@Param("sku") String sku, @Param("qty") int qty);
}
