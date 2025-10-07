package com.eplatform.b2b.order.repo;

import com.eplatform.b2b.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
