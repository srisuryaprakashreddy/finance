package com.project.finance.controller;

import com.project.finance.model.Transactions;
import com.project.finance.service.TransactionService;
import com.project.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showTransactions(@RequestParam Long userId, Model model) {
        List<Transactions> transactions = transactionService.getTransactionsByUserId(userId);
        model.addAttribute("transactions", transactions);
        model.addAttribute("transaction", new Transactions());
        model.addAttribute("userId", userId);
        return "transactions"; // transactions.html for showing and adding transactions
    }

    @PostMapping
    public String addTransaction(@RequestParam Long userId,
                                 @RequestParam double amount,
                                 @RequestParam String description,
                                 @RequestParam String category,
                                 @RequestParam String type,
                                 @RequestParam(required = false) String userConsent,
                                 Model model) {
        String result = transactionService.processTransaction(userId, amount, description, category, type, userConsent);
        if (result.startsWith("Warning")) {
            model.addAttribute("warning", result);
            return showTransactions(userId, model);
        }
        return "redirect:/transactions?userId=" + userId;
    }
}
