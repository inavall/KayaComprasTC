package com.kayacompras.controller;

import com.kayacompras.model.Product;
import com.kayacompras.model.User;
import com.kayacompras.service.ProductService;
import com.kayacompras.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping(value = { "", "/", "/index" })
    public String home(Model model, @RequestParam(required = false) String search) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.findByNameContaining(search);
        } else {
            products = productService.findAllProducts();
        }

        model.addAttribute("products", products);
        return "index";
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userService.findByUsername(auth.getName()).orElse(null);
    }

    // Página de perfil do usuário
    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        model.addAttribute("user", user);
        return "profile";
    }
}