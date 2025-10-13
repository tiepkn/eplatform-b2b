package com.eplatform.b2b.product.web;

import com.eplatform.b2b.product.domain.Product;
import com.eplatform.b2b.product.domain.SupplierProduct;
import com.eplatform.b2b.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(
                request.sku(),
                request.name(),
                request.description(),
                request.price(),
                request.category()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        List<Product> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateProduct(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.active()
        );
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProduct(@PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.ok().build();
    }

    // Supplier-related endpoints
    @PostMapping("/with-supplier")
    public ResponseEntity<Product> createProductWithSupplier(@Valid @RequestBody CreateProductWithSupplierRequest request) {
        Product product = productService.createProductWithSupplier(
                request.sku(),
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.supplierId(),
                request.supplierSku(),
                request.supplierPrice()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Product>> getProductsBySupplier(@PathVariable Long supplierId) {
        List<Product> products = productService.getProductsBySupplier(supplierId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/supplier/{supplierId}/active")
    public ResponseEntity<List<Product>> getActiveProductsBySupplier(@PathVariable Long supplierId) {
        List<Product> products = productService.getActiveProductsBySupplier(supplierId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/supplier/{supplierId}/search")
    public ResponseEntity<List<Product>> searchProductsBySupplier(@PathVariable Long supplierId, @RequestParam String q) {
        List<Product> products = productService.searchProductsBySupplier(supplierId, q);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{productId}/suppliers")
    public ResponseEntity<SupplierProduct> addSupplierToProduct(@PathVariable Long productId, @Valid @RequestBody AddSupplierRequest request) {
        SupplierProduct supplierProduct = productService.addSupplierToProduct(
                productId,
                request.supplierId(),
                request.supplierSku(),
                request.supplierPrice(),
                request.priority()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierProduct);
    }

    @GetMapping("/{productId}/suppliers")
    public ResponseEntity<List<SupplierProduct>> getProductSuppliers(@PathVariable Long productId) {
        List<SupplierProduct> suppliers = productService.getProductSuppliers(productId);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{productId}/primary-supplier")
    public ResponseEntity<SupplierProduct> getPrimarySupplier(@PathVariable Long productId) {
        return productService.getPrimarySupplierForProduct(productId)
                .map(supplier -> ResponseEntity.ok(supplier))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{productId}/suppliers/{supplierId}")
    public ResponseEntity<Void> removeSupplierFromProduct(@PathVariable Long productId, @PathVariable Long supplierId) {
        productService.removeSupplierFromProduct(productId, supplierId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/supplier-products/{supplierProductId}")
    public ResponseEntity<SupplierProduct> updateSupplierProduct(@PathVariable Long supplierProductId, @Valid @RequestBody UpdateSupplierProductRequest request) {
        productService.updateSupplierProduct(supplierProductId, request.supplierPrice(), request.priority(), request.active());
        SupplierProduct updated = productService.getProductSuppliers(supplierProductId).stream()
                .filter(sp -> sp.getId().equals(supplierProductId))
                .findFirst()
                .orElseThrow();
        return ResponseEntity.ok(updated);
    }

    // DTOs for request bodies
    public record CreateProductWithSupplierRequest(
            @NotBlank String sku,
            @NotBlank String name,
            String description,
            @NotNull @Positive BigDecimal price,
            @NotBlank String category,
            @NotNull Long supplierId,
            @NotBlank String supplierSku,
            @NotNull @Positive BigDecimal supplierPrice
    ) {}

    public record AddSupplierRequest(
            @NotNull Long supplierId,
            @NotBlank String supplierSku,
            @NotNull @Positive BigDecimal supplierPrice,
            int priority
    ) {}

    public record UpdateSupplierProductRequest(
            BigDecimal supplierPrice,
            int priority,
            boolean active
    ) {}
    public record CreateProductRequest(
            @NotBlank String sku,
            @NotBlank String name,
            String description,
            @NotNull @Positive BigDecimal price,
            @NotBlank String category
    ) {}

    public record UpdateProductRequest(
            String name,
            String description,
            BigDecimal price,
            String category,
            Boolean active
    ) {}
}
