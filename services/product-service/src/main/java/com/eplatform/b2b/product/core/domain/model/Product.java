package com.eplatform.b2b.product.core.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products", indexes = {
    @Index(name = "ux_products_sku", columnList = "sku", unique = true)
})
public class Product {
  // Getters and Setters
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(length = 1000)
    private String description;

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Getter
    @Setter
    @Column(nullable = false)
    private String category;

    @Setter
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Setter
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Setter
    @ElementCollection
    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "color", length = 7)
    private java.util.List<String> colors = new java.util.ArrayList<>();

    @Setter
    @Column(nullable = false)
    private boolean active = true;

    @Setter
    @Column(name = "supplier_id")
    private Long supplierId;

    @Setter
    @Column(name = "supplier_sku")
    private String supplierSku;

    @Setter
    @Column(name = "supplier_price", precision = 10, scale = 2)
    private BigDecimal supplierPrice;

    @Setter
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
}
