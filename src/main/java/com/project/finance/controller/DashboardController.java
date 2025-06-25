// controller/DashboardController.java (Updated)
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            model.addAttribute("totalBalance", accountService.getTotalBalance(user));
            model.addAttribute("totalIncome", transactionService.getTotalIncome(user));
            model.addAttribute("totalExpenses", transactionService.getTotalExpenses(user));
            model.addAttribute("budgets", budgetService.getBudgetsByUser(user));
            model.addAttribute("recentTransactions", transactionService.getTransactionsByUser(user));
            model.addAttribute("newAccount", new Account());
        }

        return "dashboard";
    }

    @PostMapping("/dashboard/add-account")
    public String addAccountFromDashboard(@ModelAttribute("newAccount") Account account, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user != null) {
            account.setUser(user);
            accountService.saveAccount(account);
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
