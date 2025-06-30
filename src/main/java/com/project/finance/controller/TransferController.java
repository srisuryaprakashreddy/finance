package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.service.TransferService;
import com.project.finance.service.UserService;
import com.project.finance.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    @GetMapping
    public String transferPage(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        // List of sender's accounts for dropdown
        List<Account> accounts = accountService.getAccountsByUser(user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("transfersSent", transferService.getTransfersBySender(user));
        model.addAttribute("transfersReceived", transferService.getTransfersByReceiver(user));
        return "transfer";
    }

    @PostMapping("/initiate")
    public String initiateTransfer(@RequestParam Long accountId,             // NEW: account ID
                                   @RequestParam String receiverEmail,
                                   @RequestParam Double amount,
                                   @RequestParam String pin,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User sender = userService.findByUsername(authentication.getName()).orElse(null);
        Account senderAccount = accountService.getAccountById(accountId).get();

        try {
            transferService.initiateTransfer(sender, senderAccount, receiverEmail, amount, pin);
            redirectAttributes.addFlashAttribute("success", "Transfer completed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transfer";
    }
}
