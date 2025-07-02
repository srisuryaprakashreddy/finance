package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.service.UserService;
import com.project.finance.service.AccountService;
import com.project.finance.service.TransactionService;
import com.project.finance.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BudgetService budgetService;

    // Main dashboard with all user data and accounts
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            // All accounts for the user
            List<Account> accounts = accountService.getAccountsByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("accounts", accounts);
            model.addAttribute("totalBalance", accountService.getTotalBalance(user));
            model.addAttribute("budgets", budgetService.getBudgetsByUser(user));
            model.addAttribute("recentTransactions", transactionService.getTransactionsByUser(user));
            model.addAttribute("newAccount", new Account());

            // Calculate total income and total expenses
            double totalIncome = transactionService.getTransactionsByUser(user).stream()
                    .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                    .mapToDouble(t -> t.getAmount())
                    .sum();

            double totalExpenses = transactionService.getTransactionsByUser(user).stream()
                    .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                    .mapToDouble(t -> t.getAmount())
                    .sum();

            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
        }
        return "dashboard";
    }

    // Dedicated page to show all accounts (optional)
    @GetMapping("/dashboard/accounts")
    public String showAllAccounts(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
        }
        return "dashboard-accounts"; // Create a Thymeleaf template named dashboard-accounts.html
    }

    // REST API endpoint to get all accounts as JSON (optional)
    @GetMapping("/api/dashboard/accounts")
    @ResponseBody
    public List<Account> getAllAccounts(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            return accountService.getAccountsByUser(user);
        }
        return List.of();
    }

    // Add new account from dashboard
    @PostMapping("/dashboard/add-account")
    public String addAccountFromDashboard(@ModelAttribute("newAccount") Account account, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            account.setUser(user);
            accountService.saveAccount(account);
        }
        return "redirect:/dashboard";
    }

    // Home redirects to dashboard
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
