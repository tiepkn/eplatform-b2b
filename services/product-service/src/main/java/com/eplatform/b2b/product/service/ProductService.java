package com.eplatform.b2b.product.service;

import com.eplatform.b2b.product.domain.Product;
import com.eplatform.b2b.product.domain.SupplierProduct;
import com.eplatform.b2b.product.repo.ProductRepository;
import com.eplatform.b2b.product.repo.SupplierProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierProductRepository supplierProductRepository;

    public ProductService(ProductRepository productRepository, SupplierProductRepository supplierProductRepository) {
        this.productRepository = productRepository;
        this.supplierProductRepository = supplierProductRepository;
    }

    public Product createProduct(String sku, String name, String description, BigDecimal price, String category) {
        if (productRepository.existsBySku(sku)) {
            throw new IllegalArgumentException("Product with SKU " + sku + " already exists");
        }

        Product product = new Product(sku, name, description, price, category);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findByActive(true);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryAndActive(String category, boolean active) {
        return productRepository.findByCategoryAndActive(category, active);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.findByNameOrDescriptionContaining(searchTerm, searchTerm);
    }

    public Product updateProduct(Long id, String name, String description, BigDecimal price, String category, Boolean active) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));

        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (price != null) product.setPrice(price);
        if (category != null) product.setCategory(category);
        if (active != null) product.setActive(active);

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));
        product.setActive(true);
        productRepository.save(product);
    }

    // Supplier-related methods
    public Product createProductWithSupplier(String sku, String name, String description, BigDecimal price,
                                           String category, Long supplierId, String supplierSku, BigDecimal supplierPrice) {
        if (productRepository.existsBySku(sku)) {
            throw new IllegalArgumentException("Product with SKU " + sku + " already exists");
        }

        Product product = new Product(sku, name, description, price, category, supplierId, supplierSku, supplierPrice);
        Product savedProduct = productRepository.save(product);

        // Create supplier-product relationship
        SupplierProduct supplierProduct = new SupplierProduct(savedProduct, supplierId, supplierSku, supplierPrice, 1);
        supplierProductRepository.save(supplierProduct);

        return savedProduct;
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public List<Product> getActiveProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierIdAndActive(supplierId, true);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsBySupplier(Long supplierId, String keyword) {
        return productRepository.findBySupplierIdAndKeyword(supplierId, keyword);
    }

    public SupplierProduct addSupplierToProduct(Long productId, Long supplierId, String supplierSku,
                                               BigDecimal supplierPrice, int priority) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));

        if (supplierProductRepository.existsByProductIdAndSupplierId(productId, supplierId)) {
            throw new IllegalArgumentException("Supplier " + supplierId + " already associated with product " + productId);
        }

        // If this is priority 1, update existing primary supplier
        if (priority == 1) {
            Optional<SupplierProduct> existingPrimary = supplierProductRepository
                    .findByProductIdAndSupplierIdAndPriority(productId, supplierId, 1);
            if (existingPrimary.isPresent()) {
                SupplierProduct primarySupplier = existingPrimary.get();
                primarySupplier.setPriority(2);
                supplierProductRepository.save(primarySupplier);
            }
        }

        SupplierProduct supplierProduct = new SupplierProduct(product, supplierId, supplierSku, supplierPrice, priority);
        return supplierProductRepository.save(supplierProduct);
    }

    @Transactional(readOnly = true)
    public List<SupplierProduct> getProductSuppliers(Long productId) {
        return supplierProductRepository.findByProductIdOrderByPriority(productId);
    }

    @Transactional(readOnly = true)
    public Optional<SupplierProduct> getPrimarySupplierForProduct(Long productId) {
        return supplierProductRepository.findPrimarySupplierForProduct(productId);
    }

    public void removeSupplierFromProduct(Long productId, Long supplierId) {
        SupplierProduct supplierProduct = supplierProductRepository
                .findByProductIdAndSupplierIdAndPriority(productId, supplierId, 1)
                .orElseThrow(() -> new IllegalArgumentException("Primary supplier relationship not found"));

        supplierProductRepository.delete(supplierProduct);
    }

    public void updateSupplierProduct(Long supplierProductId, BigDecimal supplierPrice, int priority, boolean active) {
        SupplierProduct supplierProduct = supplierProductRepository.findById(supplierProductId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier product relationship not found"));

        supplierProduct.setSupplierPrice(supplierPrice);
        supplierProduct.setPriority(priority);
        supplierProduct.setActive(active);
        supplierProductRepository.save(supplierProduct);
    }
}
