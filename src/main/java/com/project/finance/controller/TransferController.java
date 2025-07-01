package com.project.finance.controller;

import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.model.Transactions;
import com.project.finance.service.TransferService;
import com.project.finance.service.UserService;
import com.project.finance.service.AccountService;
import com.project.finance.service.TransactionService;
import com.project.finance.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private EmailService emailService;

    private static final String SESSION_OTP = "transferOtp";
    private static final String SESSION_TRANSFER_DATA = "transferData";

    @GetMapping
    public String transferPage(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        List<Account> accounts = accountService.getAccountsByUser(user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("transfersSent", transferService.getTransfersBySender(user));
        model.addAttribute("transfersReceived", transferService.getTransfersByReceiver(user));
        return "transfer";
    }

    // Step 1: Initiate transfer, validate PIN, send OTP
    @PostMapping("/initiate")
    public String initiateTransfer(@RequestParam Long accountId,
                                   @RequestParam String receiverEmail,
                                   @RequestParam Double amount,
                                   @RequestParam String pin,
                                   Authentication authentication,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User sender = userService.findByUsername(authentication.getName()).orElse(null);
        Account senderAccount = accountService.getAccountById(accountId).orElse(null);

        if (sender == null || senderAccount == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid sender or account.");
            return "redirect:/transfer";
        }

        // Validate PIN here only
        if (!transferService.validatePin(sender, pin)) {
            redirectAttributes.addFlashAttribute("error", "Invalid PIN.");
            return "redirect:/transfer";
        }

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute(SESSION_OTP, otp);
        session.setAttribute(SESSION_TRANSFER_DATA, new TransferData(accountId, receiverEmail, amount));

        // Send OTP to sender's email
        String subject = "Your Transfer OTP Code";
        String message = "Dear " + sender.getFirstName() + ",\n\nYour OTP code for the transfer is: " + otp + "\n\nIf you did not initiate this, please contact support immediately.";
        emailService.sendEmail(sender.getEmail(), subject, message);

        redirectAttributes.addFlashAttribute("info", "OTP sent to your registered email. Please enter the code below to confirm the transfer.");
        return "redirect:/transfer/verify";
    }

    // Step 2: Show OTP verification page
    @GetMapping("/verify")
    public String showOtpVerificationPage() {
        return "transfer-verify";
    }

    // Step 3: Verify OTP and complete transfer (NO PIN validation here)
    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String otp,
                            Authentication authentication,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        String sessionOtp = (String) session.getAttribute(SESSION_OTP);
        TransferData transferData = (TransferData) session.getAttribute(SESSION_TRANSFER_DATA);
        User sender = userService.findByUsername(authentication.getName()).orElse(null);

        if (sessionOtp == null || transferData == null || sender == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired or invalid request.");
            return "redirect:/transfer";
        }

        if (!sessionOtp.equals(otp)) {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP. Please try again.");
            return "redirect:/transfer/verify";
        }

        try {
            Account senderAccount = accountService.getAccountById(transferData.getAccountId()).orElseThrow(() -> new RuntimeException("Account not found"));
            // Actually perform the transfer
            User receiver = userService.findByEmail(transferData.getReceiverEmail()).orElse(null);
            if (receiver == null) throw new RuntimeException("Receiver not found.");
            Account receiverAccount = accountService.getMainAccountForUser(receiver); // You may need to implement this logic

            // 1. Perform the transfer using your service
            transferService.initiateTransfer(sender, senderAccount, transferData.getReceiverEmail(), transferData.getAmount(), null);

            // 2. Add transaction for sender (EXPENSE)
            Transactions senderTx = new Transactions();
            senderTx.setUser(sender);
            senderTx.setAccount(senderAccount);
            senderTx.setDate(LocalDate.now());
            senderTx.setDescription("Transfer to " + transferData.getReceiverEmail());
            senderTx.setCategory("Transfer Sent");
            senderTx.setType("EXPENSE");
            senderTx.setAmount(transferData.getAmount());
            transactionService.saveTransaction(senderTx);

            // 3. Add transaction for receiver (INCOME)
            if (receiverAccount != null) {
                Transactions receiverTx = new Transactions();
                receiverTx.setUser(receiver);
                receiverTx.setAccount(receiverAccount);
                receiverTx.setDate(LocalDate.now());
                receiverTx.setDescription("Transfer from " + sender.getEmail());
                receiverTx.setCategory("Transfer Received");
                receiverTx.setType("INCOME");
                receiverTx.setAmount(transferData.getAmount());
                transactionService.saveTransaction(receiverTx);
            }

            // Clear session attributes
            session.removeAttribute(SESSION_OTP);
            session.removeAttribute(SESSION_TRANSFER_DATA);

            redirectAttributes.addFlashAttribute("success", "Transfer completed successfully.");
            return "redirect:/transfer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/transfer/verify";
        }
    }

    // Helper class to hold transfer data in session
    public static class TransferData implements java.io.Serializable {
        private Long accountId;
        private String receiverEmail;
        private Double amount;

        public TransferData(Long accountId, String receiverEmail, Double amount) {
            this.accountId = accountId;
            this.receiverEmail = receiverEmail;
            this.amount = amount;
        }
        public Long getAccountId() { return accountId; }
        public String getReceiverEmail() { return receiverEmail; }
        public Double getAmount() { return amount; }
    }
}
