package com.eplatform.b2b.product.core.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eplatform.b2b.product.core.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository interface for Product entity operations.
 * Extends JpaRepository to provide CRUD operations and defines custom query methods
 * for product-specific business requirements such as category filtering, supplier queries,
 * and keyword searches.
 *
 * @author E-Platform B2B Team
 * @since 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**ßß
     * Finds a product by its unique SKU (Stock Keeping Unit).
     *
     * @param sku the unique stock keeping unit identifier
     * @return Optional containing the product if found, empty otherwise
     */
    Optional<Product> findBySku(String sku);

    /**
     * Finds all products belonging to a specific category.
     *
     * @param category the category name to search for
     * @return list of products in the specified category
     */
    List<Product> findByCategory(String category);

    /**
     * Finds products based on their active status.
     *
     * @param active the active status to filter by
     * @return list of products matching the active status
     */
    List<Product> findByActive(boolean active);

    /**
     * Finds products by category and active status combination.
     *
     * @param category the category name to search for
     * @param active the active status to filter by
     * @return list of products matching both category and active status
     */
    List<Product> findByCategoryAndActive(String category, boolean active);

    /**
     * Finds all products associated with a specific supplier.
     *
     * @param supplierId the unique identifier of the supplier
     * @return list of products from the specified supplier
     */
    List<Product> findBySupplierId(Long supplierId);

    /**
     * Finds products by supplier and active status combination.
     *
     * @param supplierId the unique identifier of the supplier
     * @param active the active status to filter by
     * @return list of products from the supplier matching the active status
     */
    List<Product> findBySupplierIdAndActive(Long supplierId, boolean active);

    /**
     * Searches for products where either the name or description contains
     * any of the specified search terms.
     *
     * @param name the search term for product name
     * @param description the search term for product description
     * @return list of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% OR p.description LIKE %:description%")
    List<Product> findByNameOrDescriptionContaining(@Param("name") String name, @Param("description") String description);

    /**
     * Searches for products from a specific supplier that contain the given keyword
     * in either their name or description.
     *
     * @param supplierId the unique identifier of the supplier
     * @param keyword the search keyword to look for in name or description
     * @return list of products from the supplier matching the keyword search
     */
    @Query("SELECT p FROM Product p WHERE p.supplierId = :supplierId AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    List<Product> findBySupplierIdAndKeyword(@Param("supplierId") Long supplierId, @Param("keyword") String keyword);

    /**
     * Checks if a product exists with the given SKU.
     *
     * @param sku the SKU to check for existence
     * @return true if a product with the SKU exists, false otherwise
     */
    boolean existsBySku(String sku);
}
