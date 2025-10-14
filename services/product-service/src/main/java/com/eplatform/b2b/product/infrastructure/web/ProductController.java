package com.eplatform.b2b.product.infrastructure.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;

import com.eplatform.b2b.product.core.application.commands.CreateProductRequest;
import com.eplatform.b2b.product.core.application.commands.CreateProductWithSupplierRequest;
import com.eplatform.b2b.product.core.application.commands.ProductDto;
import com.eplatform.b2b.product.core.application.commands.UpdateProductRequest;
import com.eplatform.b2b.product.core.application.service.ProductApplicationService;

import java.util.List;

/**
 * REST Controller for Product API endpoints.
 * Uses DTOs for API communication and delegates to application service.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductApplicationService productApplicationService;

    public ProductController(ProductApplicationService productApplicationService) {
        this.productApplicationService = productApplicationService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto product = productApplicationService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PostMapping("/with-supplier")
    public ResponseEntity<ProductDto> createProductWithSupplier(@Valid @RequestBody CreateProductWithSupplierRequest request) {
        ProductDto product = productApplicationService.createProductWithSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productApplicationService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDto> getProductBySku(@PathVariable String sku) {
        ProductDto product = productApplicationService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productApplicationService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String category) {
        List<ProductDto> products = productApplicationService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductDto>> getActiveProducts() {
        List<ProductDto> products = productApplicationService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String q) {
        List<ProductDto> products = productApplicationService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDto product = productApplicationService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productApplicationService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        productApplicationService.deactivateProduct(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProduct(@PathVariable Long id) {
        productApplicationService.activateProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/supplier/{supplierId}/active")
    public ResponseEntity<List<ProductDto>> getActiveProductsBySupplier(@PathVariable Long supplierId) {
        List<ProductDto> products = productApplicationService.getActiveProductsBySupplier(supplierId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/supplier/{supplierId}/search")
    public ResponseEntity<List<ProductDto>> searchProductsBySupplier(@PathVariable Long supplierId, @RequestParam String q) {
        List<ProductDto> products = productApplicationService.searchProductsBySupplier(supplierId, q);
        return ResponseEntity.ok(products);
    }
}
