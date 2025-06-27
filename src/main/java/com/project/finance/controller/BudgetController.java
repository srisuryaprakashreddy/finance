package com.project.finance.controller;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import com.project.finance.service.BudgetService;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/budget")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private UserService userService;

    private void addCommonAttributes(Model model, User user) {
        model.addAttribute("budgets", budgetService.getBudgetsByUser(user));
    }

    @GetMapping
    public String listBudgets(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            addCommonAttributes(model, user);
            model.addAttribute("budget", new Budget());
            return "budget";
        }
        return "redirect:/login";
    }

    @PostMapping("/add")
    public String addBudget(@ModelAttribute Budget budget, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            budget.setUser(user);
            budgetService.saveBudget(budget);
            redirectAttributes.addFlashAttribute("success", "Budget added successfully!");
        }
        return "redirect:/budget";
    }

    @GetMapping("/edit/{id}")
    public String showEditBudgetForm(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        Budget budget = budgetService.getBudgetById(id).orElse(null);

        if (user != null && budget != null && budget.getUser().equals(user)) {
            model.addAttribute("budget", budget);
            addCommonAttributes(model, user);
            return "budget";
        }
        return "redirect:/budget";
    }

    @PostMapping("/edit/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute Budget budget, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            budget.setUser(user);
            budget.setId(id);
            budgetService.saveBudget(budget);
            redirectAttributes.addFlashAttribute("success", "Budget updated successfully!");
        }
        return "redirect:/budget";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        // Add authorization check here
        budgetService.deleteBudget(id);
        return "redirect:/budget";
    }
}
