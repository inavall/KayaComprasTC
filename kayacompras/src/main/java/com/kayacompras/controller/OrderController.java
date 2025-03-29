package com.kayacompras.controller;

import com.kayacompras.model.Order;
import com.kayacompras.model.OrderItem;
import com.kayacompras.model.User;
import com.kayacompras.service.OrderService;
import com.kayacompras.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewOrders(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getUserOrders(currentUser);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = orderService.findOrderById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Verificar se o pedido pertence ao usuário atual ou se é um admin
        if (!order.getUser().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/order";
        }

        List<OrderItem> orderItems = orderService.getOrderItems(order);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);

        return "orderDetails";
    }

    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.createOrderFromCart(currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Pedido realizado com sucesso! Seu número de pedido é: " + order.getId());
            return "redirect:/order/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao finalizar compra: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

            // Verificar se o pedido pertence ao usuário atual
            if (!order.getUser().getId().equals(currentUser.getId()) &&
                    currentUser.getRole() != User.Role.ADMIN) {
                return "redirect:/order";
            }

            orderService.updateOrderStatus(id, Order.OrderStatus.CANCELLED);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido cancelado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cancelar pedido: " + e.getMessage());
        }

        return "redirect:/order";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userService.findByUsername(auth.getName()).orElse(null);
    }

    @ModelAttribute("currentUser")
    public User currentUser() {
        return getCurrentUser();
    }
}