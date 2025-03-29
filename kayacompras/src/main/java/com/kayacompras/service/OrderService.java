package com.kayacompras.service;

import com.kayacompras.model.*;
import com.kayacompras.repository.CartItemRepository;
import com.kayacompras.repository.OrderItemRepository;
import com.kayacompras.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrderFromCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("O carrinho está vazio");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(cartService.getCartTotal(user));

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        // Limpar o carrinho após a criação do pedido
        cartService.clearCart(user);

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<OrderItem> getOrderItems(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}