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
import java.time.LocalDate;

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
    public String transactions(Model model, Authentication authentication,
                               @RequestParam(required = false) Long accountId) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            if (accountId != null) {
                Account account = accountService.getAccountById(accountId).orElse(null);
                if (account != null && account.getUser().equals(user)) {
                    model.addAttribute("transactions", transactionService.getTransactionsByAccount(account));
                    model.addAttribute("selectedAccount", account);
                } else {
                    model.addAttribute("transactions", transactionService.getTransactionsByUser(user));
                }
            } else {
                model.addAttribute("transactions", transactionService.getTransactionsByUser(user));
            }
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            Transactions newTransaction = new Transactions();
            newTransaction.setDate(LocalDate.now());
            model.addAttribute("transaction", newTransaction);
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

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user != null) {
            transactionService.getTransactionById(id).ifPresent(transaction -> {
                if (transaction.getUser().equals(user)) {
                    transactionService.deleteTransaction(id);
                }
            });
        }
        return "redirect:/transactions";
    }
}
