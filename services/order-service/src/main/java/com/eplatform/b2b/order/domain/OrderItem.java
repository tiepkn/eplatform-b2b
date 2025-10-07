package com.eplatform.b2b.order.domain;

import jakarta.persistence.*;


@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private long priceCents;

  public Long getId() { return id; }
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public long getPriceCents() { return priceCents; }
  public void setPriceCents(long priceCents) { this.priceCents = priceCents; }
}
