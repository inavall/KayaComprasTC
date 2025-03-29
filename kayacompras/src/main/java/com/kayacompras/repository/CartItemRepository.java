package com.kayacompras.repository;

import com.kayacompras.model.CartItem;
import com.kayacompras.model.Product;
import com.kayacompras.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);
}