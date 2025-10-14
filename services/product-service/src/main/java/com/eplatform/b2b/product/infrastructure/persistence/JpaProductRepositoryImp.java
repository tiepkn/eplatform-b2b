package com.eplatform.b2b.product.infrastructure.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eplatform.b2b.product.core.domain.model.Product;
import com.eplatform.b2b.product.core.domain.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository implementation for Product domain entity.
 * Infrastructure layer implementation of the domain repository interface.
 */
public interface JpaProductRepositoryImp extends ProductRepository {

    @Override
    List<Product> findByCategory(String category);

    List<Product> findByActiveTrue();

    @Override
    List<Product> findBySupplierId(Long supplierId);

    @Override
    List<Product> findBySupplierIdAndActive(Long supplierId, boolean active);

    @Override
    @Query("SELECT p FROM Product p WHERE p.supplierId = :supplierId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> findBySupplierIdAndKeyword(@Param("supplierId") Long supplierId, @Param("keyword") String keyword);

    @Override
    boolean existsBySku(String sku);

    @Override
    default Optional<Product> findBySku(String sku) {
        return findAll().stream()
                .filter(product -> sku.equals(product.getSku()))
                .findFirst();
    }
}
