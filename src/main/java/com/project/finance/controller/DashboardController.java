package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {
    @Autowired private UserService userService;
    @Autowired private AccountService accountService;
    @Autowired private TransactionService transactionService;
    @Autowired private BudgetService budgetService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("accounts", accountService.getAccountsByUser(user));
        model.addAttribute("totalBalance", accountService.getTotalBalance(user));
        model.addAttribute("budgets", budgetService.getBudgetsByUser(user));
        model.addAttribute("recentTransactions", transactionService.getRecentTransactions(user, 10));
        model.addAttribute("totalIncome", transactionService.getTotalIncomeForMonth(user, LocalDate.now()));
        model.addAttribute("totalExpenses", transactionService.getTotalExpensesForMonth(user, LocalDate.now()));
        return "dashboard";
    }
}
