package com.kayacompras.repository;

import com.kayacompras.model.Order;
import com.kayacompras.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}