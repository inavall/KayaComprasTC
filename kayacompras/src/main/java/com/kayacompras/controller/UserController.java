package com.kayacompras.controller;

import com.kayacompras.model.User;
import com.kayacompras.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registro bem-sucedido! Faça login para continuar.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao registrar: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        // Spring Security irá gerenciar o processo de login
        return "login";
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
