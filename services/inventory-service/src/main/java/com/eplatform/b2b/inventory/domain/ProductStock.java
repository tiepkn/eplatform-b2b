package com.eplatform.b2b.inventory.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "product_stock", indexes = {
    @Index(name = "ux_stock_sku", columnList = "sku", unique = true)
})
public class ProductStock {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String sku;

  @Column(nullable = false)
  private int available;

  @Column(nullable = false)
  private int reserved;

  @Version
  private Long version;

  protected ProductStock() {}

  public ProductStock(String sku, int available) {
    this.sku = sku;
    this.available = available;
    this.reserved = 0;
  }

  public Long getId() { return id; }
  public String getSku() { return sku; }
  public int getAvailable() { return available; }
  public int getReserved() { return reserved; }
  public Long getVersion() { return version; }

  public void add(int qty) { this.available += qty; }
  public boolean reserve(int qty) {
    if (qty <= 0) return false;
    if (this.available < qty) return false;
    this.available -= qty;
    return true;
  }

  public void lock(int qty) {
    this.available -= qty;
    this.reserved += qty;
  }

  public void confirmReservation(int qty) {
    this.reserved -= qty;
  }

  public void release(int qty) {
    this.available += qty;
    this.reserved -= qty;
  }
}
