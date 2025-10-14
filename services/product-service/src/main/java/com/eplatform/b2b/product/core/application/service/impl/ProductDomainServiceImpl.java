package com.eplatform.b2b.product.core.application.service.impl;

import com.eplatform.b2b.product.core.domain.model.Product;
import com.eplatform.b2b.product.core.application.service.ProductDomainService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Domain service implementation for product business logic.
 * Contains business rules that don't naturally belong in entities.
 */
@Service
public class ProductDomainServiceImpl implements ProductDomainService {

    @Override
    public void validateProductData(String sku, String name, BigDecimal price, String category) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be null or empty");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }

        // Additional business rules
        if (sku.length() < 3) {
            throw new IllegalArgumentException("Product SKU must be at least 3 characters long");
        }

        if (price.compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("Product price cannot exceed $1,000,000");
        }
    }

    @Override
    public boolean canDeleteProduct(Product product) {
        // Business rule: products with orders cannot be deleted
        // In a real application, this would check for existing orders
        // For now, we'll allow deletion of all products
        return true;
    }

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal basePrice, String category) {
        // Apply category-specific pricing rules
        if (category != null) {
            switch (category.toLowerCase()) {
                case "premium":
                    return basePrice.multiply(new BigDecimal("1.2")); // 20% markup
                case "luxury":
                    return basePrice.multiply(new BigDecimal("1.5")); // 50% markup
                case "budget":
                    return basePrice.multiply(new BigDecimal("0.9")); // 10% discount
                default:
                    return basePrice;
            }
        }
        return basePrice;
    }

    @Override
    public String generateNextSku(String category, List<String> existingSkus) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        String prefix = category.substring(0, Math.min(3, category.length())).toUpperCase();
        int counter = 1;

        while (true) {
            String candidateSku = prefix + String.format("%03d", counter);
            if (!existingSkus.contains(candidateSku)) {
                return candidateSku;
            }
            counter++;

            // Prevent infinite loop
            if (counter > 9999) {
                throw new IllegalStateException("Cannot generate unique SKU for category: " + category);
            }
        }
    }

    @Override
    public void validateProductUpdate(Product existingProduct, Product updatedProduct) {
        // Validate that SKU uniqueness is maintained
        if (!existingProduct.getSku().equals(updatedProduct.getSku())) {
            // SKU is being changed, need to validate uniqueness
            // In a real implementation, this would check against repository
            if (updatedProduct.getSku() == null || updatedProduct.getSku().trim().isEmpty()) {
                throw new IllegalArgumentException("Product SKU cannot be null or empty");
            }
        }

        // Validate price changes
        if (updatedProduct.getPrice() != null &&
            updatedProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        // Validate category
        if (updatedProduct.getCategory() != null &&
            updatedProduct.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be empty");
        }
    }
}
