package com.project.finance.service;

import com.project.finance.model.Transfer;
import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.model.Transactions;
import com.project.finance.repository.TransferRepository;
import com.project.finance.repository.UserRepository;
import com.project.finance.repository.AccountRepository;
import com.project.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public boolean initiateTransfer(User sender, String receiverEmail, Double amount, String rawPin) {
        if (!userService.checkPin(sender, rawPin)) {
            throw new IllegalArgumentException("Invalid PIN");
        }

        Optional<User> receiverOpt = userRepository.findByEmail(receiverEmail);
        if (receiverOpt.isEmpty()) throw new IllegalArgumentException("Receiver not found");
        User receiver = receiverOpt.get();

        List<Account> senderAccounts = accountRepository.findByUser(sender);
        if (senderAccounts.isEmpty()) throw new IllegalArgumentException("Sender has no accounts");
        Account senderAccount = senderAccounts.get(0);
        if (senderAccount.getBalance() < amount) throw new IllegalArgumentException("Insufficient balance");

        List<Account> receiverAccounts = accountRepository.findByUser(receiver);
        if (receiverAccounts.isEmpty()) throw new IllegalArgumentException("Receiver has no accounts");
        Account receiverAccount = receiverAccounts.get(0);

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setReceiver(receiver);
        transfer.setAmount(amount);
        transfer.setCreatedAt(LocalDateTime.now());
        transfer.setCompleted(true);
        transferRepository.save(transfer);

        Transactions senderTx = new Transactions();
        senderTx.setUser(sender);
        senderTx.setAccount(senderAccount);
        senderTx.setAmount(amount);
        senderTx.setType("EXPENSE");
        senderTx.setDescription("Transfer to " + receiver.getEmail());
        senderTx.setCategory("Transfer");
        senderTx.setDate(LocalDate.now());
        senderTx.setIsDebit(true);
        transactionRepository.save(senderTx);

        Transactions receiverTx = new Transactions();
        receiverTx.setUser(receiver);
        receiverTx.setAccount(receiverAccount);
        receiverTx.setAmount(amount);
        receiverTx.setType("INCOME");
        receiverTx.setDescription("Transfer from " + sender.getEmail());
        receiverTx.setCategory("Transfer");
        receiverTx.setDate(LocalDate.now());
        receiverTx.setIsDebit(false);
        transactionRepository.save(receiverTx);

        return true;
    }

    public List<Transfer> getTransfersBySender(User sender) {
        return transferRepository.findBySender(sender);
    }

    public List<Transfer> getTransfersByReceiver(User receiver) {
        return transferRepository.findByReceiver(receiver);
    }
}
