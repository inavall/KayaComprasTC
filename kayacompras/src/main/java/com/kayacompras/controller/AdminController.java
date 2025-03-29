package com.kayacompras.controller;

import com.kayacompras.model.Order;
import com.kayacompras.model.Product;
import com.kayacompras.model.User;
import com.kayacompras.service.OrderService;
import com.kayacompras.service.ProductService;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        List<Order> recentOrders = orderService.getAllOrders();
        List<User> users = userService.findAllUsers();

        model.addAttribute("orderCount", recentOrders.size());
        model.addAttribute("userCount", users.size());
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }

    // Gerenciamento de Produtos
    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.findAllProducts();
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/productForm";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado com sucesso!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar produto: " + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        model.addAttribute("product", product);
        return "admin/productForm";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product,
            RedirectAttributes redirectAttributes) {
        try {
            product.setId(id);
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Produto atualizado com sucesso!");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar produto: " + e.getMessage());
            return "redirect:/admin/products/edit/" + id;
        }
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir produto: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Gerenciamento de Pedidos
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderService.getOrderItems(order));
        return "admin/orderDetails";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
            @RequestParam Order.OrderStatus status,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Status do pedido atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar status: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    // Gerenciamento de Usuários
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userService.findByUsername(auth.getName()).orElse(null);
    }
}