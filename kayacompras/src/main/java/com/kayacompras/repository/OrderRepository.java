package com.kayacompras.repository;

import com.kayacompras.model.Order;
import com.kayacompras.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserOrderByCreatedAtDesc(User user);
}