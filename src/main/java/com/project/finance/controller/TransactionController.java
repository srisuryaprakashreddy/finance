// controller/TransactionController.java
package com.project.finance.controller;

import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.service.TransactionService;
import com.project.finance.service.UserService;
import com.project.finance.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @GetMapping
    public String transactions(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user != null) {
            model.addAttribute("transactions", transactionService.getTransactionsByUser(user));
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            model.addAttribute("transaction", new Transactions());
        }

        return "transactions";
    }

    @PostMapping("/add")
    public String addTransaction(@ModelAttribute Transactions transaction, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);

        if (user != null) {
            transaction.setUser(user);
            transactionService.saveTransaction(transaction);
        }

        return "redirect:/transactions";
    }
}
