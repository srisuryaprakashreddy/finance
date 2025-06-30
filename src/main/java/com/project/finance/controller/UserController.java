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

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, Authentication authentication) {
        User existingUser = userService.findByUsername(authentication.getName()).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            userService.save(existingUser);
        }
        return "redirect:/user/profile";
    }

    // --- Change Password ---
    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null || !userService.checkPassword(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/user/change-password";
        }
        userService.updatePassword(user, newPassword);

        // Optionally, you can log out the user here if you want to force re-login immediately
        // SecurityContextHolder.clearContext();

        redirectAttributes.addFlashAttribute("success", "Password changed successfully. Please log in again.");
        return "redirect:/login";
    }

    // --- Change PIN ---
    @GetMapping("/change-pin")
    public String changePinForm() {
        return "change-pin";
    }

    @PostMapping("/change-pin")
    public String changePin(
            @RequestParam String currentPin,
            @RequestParam String newPin,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null || !userService.checkPin(user, currentPin)) {
            redirectAttributes.addFlashAttribute("error", "Current PIN is incorrect.");
            return "redirect:/user/change-pin";
        }
        userService.updatePin(user, newPin);

        // Optionally, you can log out the user here if you want to force re-login immediately
        // SecurityContextHolder.clearContext();

        redirectAttributes.addFlashAttribute("success", "PIN changed successfully. Please log in again.");
        return "redirect:/login";
    }
}
