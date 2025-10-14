// package com.eplatform.b2b.product.repo;

// import com.eplatform.b2b.product.domain.Product;
// import com.eplatform.b2b.product.domain.SupplierProduct;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

//     List<SupplierProduct> findByProduct(Product product);

//     List<SupplierProduct> findByProductId(Long productId);

//     List<SupplierProduct> findBySupplierId(Long supplierId);

//     List<SupplierProduct> findByProductIdAndActive(Long productId, boolean active);

//     List<SupplierProduct> findBySupplierIdAndActive(Long supplierId, boolean active);

//     Optional<SupplierProduct> findByProductAndSupplierIdAndPriority(Product product, Long supplierId, int priority);

//     Optional<SupplierProduct> findByProductIdAndSupplierIdAndPriority(Long productId, Long supplierId, int priority);

//     @Query("SELECT sp FROM SupplierProduct sp WHERE sp.product.id = :productId AND sp.priority = 1 AND sp.active = true")
//     Optional<SupplierProduct> findPrimarySupplierForProduct(@Param("productId") Long productId);

//     @Query("SELECT sp FROM SupplierProduct sp WHERE sp.supplierId = :supplierId AND sp.priority = 1 AND sp.active = true")
//     List<SupplierProduct> findPrimaryProductsForSupplier(@Param("supplierId") Long supplierId);

//     @Query("SELECT COUNT(sp) FROM SupplierProduct sp WHERE sp.product.id = :productId AND sp.active = true")
//     long countActiveSuppliersForProduct(@Param("productId") Long productId);

//     @Query("SELECT COUNT(sp) FROM SupplierProduct sp WHERE sp.supplierId = :supplierId AND sp.active = true")
//     long countActiveProductsForSupplier(@Param("supplierId") Long supplierId);

//     List<SupplierProduct> findByProductIdOrderByPriority(Long productId);

//     boolean existsByProductIdAndSupplierId(Long productId, Long supplierId);
// }
