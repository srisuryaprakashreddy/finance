package com.project.finance.service;

import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.model.Transfer;
import com.project.finance.repository.AccountRepository;
import com.project.finance.repository.TransferRepository;
import com.project.finance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransferService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransferRepository transferRepository;

    /**
     * Validates the sender's PIN.
     */
    public boolean validatePin(User sender, String pin) {
        if (sender == null || pin == null) return false;
        String storedPinHash = sender.getPin();
        if (storedPinHash == null) return false;
        return passwordEncoder.matches(pin, storedPinHash);
    }

    /**
     * Initiates a transfer from sender to receiver.
     * If pin is not null, validates the PIN.
     * Throws IllegalArgumentException on failure.
     */
    @Transactional
    public void initiateTransfer(User sender, Account senderAccount, String receiverEmail, Double amount, String pin) {
        if (pin != null) {
            if (!validatePin(sender, pin)) {
                throw new IllegalArgumentException("Invalid PIN");
            }
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid amount.");
        }

        if (senderAccount == null) {
            throw new IllegalArgumentException("Sender account not found.");
        }

        if (senderAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // Find receiver by email
        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        // Find receiver's default account (or pick first)
        List<Account> receiverAccounts = accountRepository.findByUser(receiver);
        if (receiverAccounts.isEmpty()) {
            throw new IllegalArgumentException("Receiver has no account.");
        }
        Account receiverAccount = receiverAccounts.get(0);

        // Perform transfer
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        // Save accounts
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        // Record transfer (optional, but recommended)
        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setSenderAccount(senderAccount);
        transfer.setReceiver(receiver);
        transfer.setReceiverAccount(receiverAccount);
        transfer.setAmount(amount);
        transfer.setCompleted(true);
        transferRepository.save(transfer);
    }

    /**
     * Returns transfers sent by a user.
     */
    public List<Transfer> getTransfersBySender(User sender) {
        return transferRepository.findBySender(sender);
    }

    /**
     * Returns transfers received by a user.
     */
    public List<Transfer> getTransfersByReceiver(User receiver) {
        return transferRepository.findByReceiver(receiver);
    }
}
