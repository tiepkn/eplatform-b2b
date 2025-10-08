package com.eplatform.b2b.product.repo;

import com.eplatform.b2b.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByActive(boolean active);

    List<Product> findByCategoryAndActive(String category, boolean active);

    List<Product> findBySupplierId(Long supplierId);

    List<Product> findBySupplierIdAndActive(Long supplierId, boolean active);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% OR p.description LIKE %:description%")
    List<Product> findByNameOrDescriptionContaining(@Param("name") String name, @Param("description") String description);

    @Query("SELECT p FROM Product p WHERE p.supplierId = :supplierId AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    List<Product> findBySupplierIdAndKeyword(@Param("supplierId") Long supplierId, @Param("keyword") String keyword);

    boolean existsBySku(String sku);
}
