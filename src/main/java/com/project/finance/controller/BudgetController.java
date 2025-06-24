package com.project.finance.controller;

import com.project.finance.model.Budget;
import com.project.finance.service.BudgetService;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserService userService;

    // Display budget form with userId as query parameter
    @GetMapping
    public String showBudgetForm(@RequestParam("userId") Long userId, Model model) {
        Budget budget = budgetService.getBudgetByUserId(userId);
        if (budget == null) {
            budget = new Budget(); // new budget object if none found
        }

        model.addAttribute("budget", budget);
        model.addAttribute("userId", userId);
        return "budgetForm"; // Thymeleaf form view
    }

    // Save or update budget and redirect to dashboard with userId
    @PostMapping
    public String saveBudget(@RequestParam("userId") Long userId,
                             @RequestParam("budgetAmount") double budgetAmount,
                             @RequestParam("startDate") String startDate,
                             @RequestParam("endDate") String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        budgetService.createOrUpdateBudget(userId, budgetAmount, start, end);

        // Redirect back to dashboard, maintaining userId in query
        return "redirect:/dashboard?userId=" + userId;
    }
}
