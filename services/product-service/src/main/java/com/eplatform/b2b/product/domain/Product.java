package com.eplatform.b2b.product.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "ux_products_sku", columnList = "sku", unique = true),
    @Index(name = "idx_products_category", columnList = "category"),
    @Index(name = "idx_products_supplier", columnList = "supplier_id")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_sku")
    private String supplierSku;

    @Column(name = "supplier_price", precision = 10, scale = 2)
    private BigDecimal supplierPrice;

    @Column(name = "is_primary_supplier")
    private boolean primarySupplier = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    protected Product() {}

    public Product(String sku, String name, String description, BigDecimal price, String category) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Product(String sku, String name, String description, BigDecimal price, String category,
                  Long supplierId, String supplierSku, BigDecimal supplierPrice) {
        this(sku, name, description, price, category);
        this.supplierId = supplierId;
        this.supplierSku = supplierSku;
        this.supplierPrice = supplierPrice;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isActive() { return active; }
    public Long getSupplierId() { return supplierId; }
    public String getSupplierSku() { return supplierSku; }
    public BigDecimal getSupplierPrice() { return supplierPrice; }
    public boolean isPrimarySupplier() { return primarySupplier; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setActive(boolean active) { this.active = active; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public void setSupplierSku(String supplierSku) { this.supplierSku = supplierSku; }
    public void setSupplierPrice(BigDecimal supplierPrice) { this.supplierPrice = supplierPrice; }
    public void setPrimarySupplier(boolean primarySupplier) { this.primarySupplier = primarySupplier; }
}
