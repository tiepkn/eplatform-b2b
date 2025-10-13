package com.eplatform.b2b.product.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_products", indexes = {
    @Index(name = "idx_supplier_products_product", columnList = "product_id"),
    @Index(name = "idx_supplier_products_supplier", columnList = "supplier_id"),
    @Index(name = "idx_supplier_products_priority", columnList = "priority"),
    @Index(name = "idx_supplier_products_active", columnList = "is_active")
})
public class SupplierProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "supplier_sku", nullable = false)
    private String supplierSku;

    @Column(name = "supplier_price", precision = 10, scale = 2)
    private BigDecimal supplierPrice;

    @Column(name = "priority", nullable = false)
    private int priority = 1; // 1 = primary, 2 = secondary, etc.

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "supplier_product_name")
    private String supplierProductName;

    @Column(name = "supplier_description", length = 1000)
    private String supplierDescription;

    @Column(name = "minimum_order_quantity")
    private Integer minimumOrderQuantity;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    protected SupplierProduct() {}

    public SupplierProduct(Product product, Long supplierId, String supplierSku,
                          BigDecimal supplierPrice, int priority) {
        this.product = product;
        this.supplierId = supplierId;
        this.supplierSku = supplierSku;
        this.supplierPrice = supplierPrice;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public Long getSupplierId() { return supplierId; }
    public String getSupplierSku() { return supplierSku; }
    public BigDecimal getSupplierPrice() { return supplierPrice; }
    public int getPriority() { return priority; }
    public boolean isActive() { return active; }
    public String getSupplierProductName() { return supplierProductName; }
    public String getSupplierDescription() { return supplierDescription; }
    public Integer getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public void setProduct(Product product) { this.product = product; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public void setSupplierSku(String supplierSku) { this.supplierSku = supplierSku; }
    public void setSupplierPrice(BigDecimal supplierPrice) { this.supplierPrice = supplierPrice; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setActive(boolean active) { this.active = active; }
    public void setSupplierProductName(String supplierProductName) { this.supplierProductName = supplierProductName; }
    public void setSupplierDescription(String supplierDescription) { this.supplierDescription = supplierDescription; }
    public void setMinimumOrderQuantity(Integer minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public boolean isPrimarySupplier() {
        return priority == 1;
    }
}
