package com.project.finance.controller;

import com.project.finance.model.Account;
import com.project.finance.model.Budget;
import com.project.finance.model.User;
import com.project.finance.service.AccountService;
import com.project.finance.service.BudgetService;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam("userId") Long userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) return "redirect:/login";

        Account account = accountService.getAccountByUserId(userId);
        Budget budget = budgetService.getBudgetByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("account", account != null ? account : new Account());
        model.addAttribute("budget", budget != null ? budget : new Budget());

        return "dashboard"; // dashboard.html
    }
}
