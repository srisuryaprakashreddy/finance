package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, Authentication authentication, RedirectAttributes redirectAttributes) {
        User existingUser = userService.findByUsername(authentication.getName()).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            userService.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        }
        return "redirect:/user/profile";
    }
}
