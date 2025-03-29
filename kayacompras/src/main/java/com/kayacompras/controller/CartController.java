package com.kayacompras.controller;

import com.kayacompras.model.CartItem;
import com.kayacompras.model.User;
import com.kayacompras.service.CartService;
import com.kayacompras.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItems(currentUser);
        BigDecimal total = cartService.getCartTotal(currentUser);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            cartService.addToCart(currentUser, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado ao carrinho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar produto: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/update/{cartItemId}")
    public String updateCartItem(@PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            cartService.updateCartItemQuantity(cartItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Carrinho atualizado!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar carrinho: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeCartItem(@PathVariable Long cartItemId,
            RedirectAttributes redirectAttributes) {
        try {
            cartService.removeCartItem(cartItemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removido do carrinho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao remover item: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            cartService.clearCart(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Carrinho esvaziado!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao esvaziar carrinho: " + e.getMessage());
        }

        return "redirect:/cart";
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