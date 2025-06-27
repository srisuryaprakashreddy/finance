package com.project.finance.service;

import com.project.finance.model.*;
import com.project.finance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferService {
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;

    @Transactional
    public void initiateTransfer(User sender, String receiverEmail, Double amount, String rawPin) {
        if (!userService.checkPin(sender, rawPin)) {
            throw new IllegalArgumentException("Invalid PIN provided.");
        }

        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new IllegalArgumentException("Receiver with email " + receiverEmail + " not found."));

        // For simplicity, we use the first account. A real app would let the user choose.
        Account senderAccount = accountRepository.findByUser(sender).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Sender does not have an account."));

        if (senderAccount.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance.");
        }

        Account receiverAccount = accountRepository.findByUser(receiver).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Receiver does not have an account."));

        // Create sender DEBIT transaction
        Transactions senderTx = new Transactions();
        senderTx.setUser(sender);
        senderTx.setAccount(senderAccount);
        senderTx.setAmount(amount);
        senderTx.setType("DEBIT");
        senderTx.setDescription("Transfer to " + receiver.getEmail());
        senderTx.setCategory("Transfer");
        senderTx.setDate(LocalDate.now());
        transactionService.saveTransaction(senderTx); // This updates the balance

        // Create receiver CREDIT transaction
        Transactions receiverTx = new Transactions();
        receiverTx.setUser(receiver);
        receiverTx.setAccount(receiverAccount);
        receiverTx.setAmount(amount);
        receiverTx.setType("CREDIT");
        receiverTx.setDescription("Transfer from " + sender.getEmail());
        receiverTx.setCategory("Transfer");
        receiverTx.setDate(LocalDate.now());
        transactionService.saveTransaction(receiverTx); // This updates the balance

        // Log the transfer event
        Transfer transferLog = new Transfer();
        transferLog.setSender(sender);
        transferLog.setReceiverEmail(receiverEmail);
        transferLog.setAmount(amount);
        transferLog.setTimestamp(LocalDateTime.now());
        transferLog.setStatus("COMPLETED");
        transferRepository.save(transferLog);
    }

    public List<Transfer> getTransfersBySender(User sender) {
        return transferRepository.findBySenderOrderByTimestampDesc(sender);
    }

    public List<Transfer> getTransfersByReceiverEmail(String email) {
        return transferRepository.findByReceiverEmailOrderByTimestampDesc(email);
    }
}
