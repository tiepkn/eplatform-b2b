package com.eplatform.b2b.product.core.application.service;

import com.eplatform.b2b.product.core.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;

/**
 * Domain service interface for product business logic.
{{ ... }}
 */
public interface ProductDomainService {

    /**
     * Validates product data for business rules.
     * @param sku the product SKU
     * @param name the product name
     * @param price the product price
     * @param category the product category
     * @throws IllegalArgumentException if validation fails
     */
    void validateProductData(String sku, String name, BigDecimal price, String category);

    /**
     * Checks if a product can be deleted based on business rules.
     * @param product the product to check
     * @return true if product can be deleted
     */
    boolean canDeleteProduct(Product product);

    /**
     * Calculates the final price of a product including any business rules.
     * @param basePrice the base price
     * @param category the product category
     * @return the calculated final price
     */
    BigDecimal calculateFinalPrice(BigDecimal basePrice, String category);

    /**
     * Generates the next SKU based on category and existing products.
     * @param category the product category
     * @param existingSkus existing SKUs to avoid conflicts
     * @return a unique SKU for the category
     */
    String generateNextSku(String category, List<String> existingSkus);

    /**
     * Validates if a product update is allowed based on business rules.
     * @param existingProduct the current product
     * @param updatedProduct the product with updates
     * @throws IllegalArgumentException if update is not allowed
     */
    void validateProductUpdate(Product existingProduct, Product updatedProduct);
}
