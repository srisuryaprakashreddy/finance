// controller/UserController.java
package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
