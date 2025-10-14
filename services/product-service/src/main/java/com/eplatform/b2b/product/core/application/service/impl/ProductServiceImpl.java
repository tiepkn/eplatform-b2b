package com.eplatform.b2b.product.core.application.service.impl;

import com.eplatform.b2b.product.core.application.commands.CreateProductRequest;
import com.eplatform.b2b.product.core.application.commands.CreateProductWithSupplierRequest;
import com.eplatform.b2b.product.core.application.commands.ProductDto;
import com.eplatform.b2b.product.core.application.commands.UpdateProductRequest;
import com.eplatform.b2b.product.core.application.service.ProductApplicationService;
import com.eplatform.b2b.product.core.application.service.ProductDomainService;
import com.eplatform.b2b.product.core.domain.model.Product;
import com.eplatform.b2b.product.core.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class ProductServiceImpl implements ProductApplicationService {

    private final ProductRepository productRepository;
    private final ProductDomainService productDomainService;

    public ProductServiceImpl(ProductRepository productRepository, ProductDomainService productDomainService) {
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    @Override
    public ProductDto createProduct(CreateProductRequest request) {
        // Validate business rules
        productDomainService.validateProductData(
            request.sku(), request.name(), request.price(), request.category()
        );

        // Check if SKU already exists
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU " + request.sku() + " already exists");
        }

        // Create domain entity
        Product product = new Product(
            request.sku(),
            request.name(),
            request.description(),
            request.price(),
            request.category()
        );

        // Save product
        Product savedProduct = productRepository.save(product);

        // Map to DTO and return
        return mapToDto(savedProduct);
    }

    @Override
    public ProductDto createProductWithSupplier(CreateProductWithSupplierRequest request) {
        // For now, delegate to createProduct
        CreateProductRequest basicRequest = new CreateProductRequest(
            request.sku(),
            request.name(),
            request.description(),
            request.price(),
            request.category()
        );

        return createProduct(basicRequest);
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        return mapToDto(product);
    }

    @Override
    public ProductDto getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
        return mapToDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getActiveProducts() {
        return productRepository.findByActive(true).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProducts(String query) {
        return productRepository.findByNameOrDescriptionContaining(query, query).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        // Update fields if provided
        if (request.name() != null) {
            existingProduct.setName(request.name());
        }
        if (request.description() != null) {
            existingProduct.setDescription(request.description());
        }
        if (request.price() != null) {
            existingProduct.setPrice(request.price());
        }
        if (request.category() != null) {
            existingProduct.setCategory(request.category());
        }

        // Validate updated product
        productDomainService.validateProductUpdate(existingProduct, existingProduct);

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }

        if (!productDomainService.canDeleteProduct(productRepository.findById(id).get())) {
            throw new IllegalArgumentException("Cannot delete product with ID: " + id);
        }

        productRepository.deleteById(id);
    }

    @Override
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    public List<ProductDto> getProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierId(supplierId).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getActiveProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierIdAndActive(supplierId, true).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProductsBySupplier(Long supplierId, String keyword) {
        return productRepository.findBySupplierIdAndKeyword(supplierId, keyword).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    private ProductDto mapToDto(Product product) {
        return new ProductDto(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getSupplierId(),
            product.getSupplierSku(),
            product.getSupplierPrice(),
            product.isActive(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
