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

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String budget(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("budgets", budgetService.getBudgetsByUser(user));
            model.addAttribute("budget", new Budget());
        }
        return "budget";
    }

    @PostMapping("/add")
    public String addBudget(@ModelAttribute Budget budget, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            budget.setUser(user);
            budget.setSpent(0.0);
            budgetService.saveBudget(budget);
        }
        return "redirect:/budget";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return "redirect:/budget";
    }
    @GetMapping("/edit/{id}")
    public String showEditBudgetForm(@PathVariable Long id, Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        Budget budget = budgetService.getBudgetById(id);
        // Optional: check if the budget belongs to the current user
        if (budget != null && user != null && budget.getUser().getId().equals(user.getId())) {
            model.addAttribute("budget", budget);
            model.addAttribute("budgets", budgetService.getBudgetsByUser(user));
            model.addAttribute("editMode", true);
            return "budget";
        }
        return "redirect:/budget";
    }

    // Handle the update POST
    @PostMapping("/update")
    public String updateBudget(@ModelAttribute Budget budget, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            budget.setUser(user);
            // Optionally: preserve the 'spent' field if not changed in the form
            Budget existing = budgetService.getBudgetById(budget.getId());
            if(existing != null) {
                budget.setSpent(existing.getSpent());
            }
            budgetService.saveBudget(budget);
        }
        return "redirect:/budget";
    }
}
