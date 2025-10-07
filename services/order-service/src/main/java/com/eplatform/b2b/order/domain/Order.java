package com.eplatform.b2b.order.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private long totalAmountCents;

  @Column(nullable = false)
  private String status; // CREATED, RESERVED, PAID, FAILED

  private String paymentTransactionId;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<OrderItem> items = new ArrayList<>();

  public String getId() { return id; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public long getTotalAmountCents() { return totalAmountCents; }
  public void setTotalAmountCents(long totalAmountCents) { this.totalAmountCents = totalAmountCents; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getPaymentTransactionId() { return paymentTransactionId; }
  public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
  public List<OrderItem> getItems() { return items; }

  public void addItem(OrderItem item) {
    item.setOrder(this);
    items.add(item);
  }
}
