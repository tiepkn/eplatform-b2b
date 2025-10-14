package com.eplatform.b2b.product.core.application.service;

import com.eplatform.b2b.product.core.application.commands.ProductDto;
import com.eplatform.b2b.product.core.application.commands.CreateProductRequest;
import com.eplatform.b2b.product.core.application.commands.UpdateProductRequest;
import java.util.List;

/**
 * Application service interface for product use cases.
 * Coordinates between domain services, repositories, and external systems.
 * This is the entry point for product-related operations.
 */
public interface ProductApplicationService {

    /**
     * Creates a new product.
     * @param request the product creation request
     * @return the created product DTO
     */
    ProductDto createProduct(CreateProductRequest request);

    /**
     * Creates a product with supplier information.
     * @param request the product creation request with supplier data
     * @return the created product DTO
     */
    ProductDto createProductWithSupplier(com.eplatform.b2b.product.core.application.commands.CreateProductWithSupplierRequest request);

    /**
     * Retrieves a product by its ID.
     * @param id the product ID
     * @return the product DTO if found
     */
    ProductDto getProductById(Long id);

    /**
     * Retrieves a product by its SKU.
     * @param sku the product SKU
     * @return the product DTO if found
     */
    ProductDto getProductBySku(String sku);

    /**
     * Retrieves all products.
     * @return list of all product DTOs
     */
    List<ProductDto> getAllProducts();

    /**
     * Retrieves products by category.
     * @param category the category to filter by
     * @return list of product DTOs in the category
     */
    List<ProductDto> getProductsByCategory(String category);

    /**
     * Retrieves only active products.
     * @return list of active product DTOs
     */
    List<ProductDto> getActiveProducts();

    /**
     * Searches products by keyword.
     * @param query the search query
     * @return list of matching product DTOs
     */
    List<ProductDto> searchProducts(String query);

    /**
     * Updates an existing product.
     * @param id the product ID to update
     * @param request the update request
     * @return the updated product DTO
     */
    ProductDto updateProduct(Long id, UpdateProductRequest request);

    /**
     * Deletes a product by ID.
     * @param id the product ID to delete
     */
    void deleteProduct(Long id);

    /**
     * Deactivates a product.
     * @param id the product ID to deactivate
     */
    void deactivateProduct(Long id);

    /**
     * Activates a product.
     * @param id the product ID to activate
     */
    void activateProduct(Long id);

    /**
     * Retrieves products by supplier.
     * @param supplierId the supplier ID
     * @return list of product DTOs from the supplier
     */
    List<ProductDto> getProductsBySupplier(Long supplierId);

    /**
     * Retrieves active products by supplier.
     * @param supplierId the supplier ID
     * @return list of active product DTOs from the supplier
     */
    List<ProductDto> getActiveProductsBySupplier(Long supplierId);

    /**
     * Searches products by supplier with keyword.
     * @param supplierId the supplier ID
     * @param keyword the search keyword
     * @return list of matching product DTOs
     */
    List<ProductDto> searchProductsBySupplier(Long supplierId, String keyword);
}
