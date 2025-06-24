// controller/AuthController.java
package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        if (userService.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/register";
        }

        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful");
        return "redirect:/login";
    }
}
