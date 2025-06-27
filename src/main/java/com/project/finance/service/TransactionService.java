package com.project.finance.service;

import com.project.finance.model.Account;
import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import com.project.finance.repository.AccountRepository;
import com.project.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Transactions saveTransaction(Transactions transaction) {
        Account account = transaction.getAccount();
        if (account == null) {
            throw new IllegalStateException("Transaction must be associated with an account.");
        }

        if ("CREDIT".equals(transaction.getType())) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else if ("DEBIT".equals(transaction.getType())) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        }
        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresent(transaction -> {
            Account account = transaction.getAccount();
            if ("CREDIT".equals(transaction.getType())) {
                account.setBalance(account.getBalance() - transaction.getAmount());
            } else if ("DEBIT".equals(transaction.getType())) {
                account.setBalance(account.getBalance() + transaction.getAmount());
            }
            accountRepository.save(account);
            transactionRepository.delete(transaction);
        });
    }

    public List<Transactions> getTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public List<Transactions> getRecentTransactions(User user, int limit) {
        List<Transactions> allTransactions = transactionRepository.findByUser(user);
        // Simple sort by date and limit, for production consider a paginated query
        allTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        return allTransactions.stream().limit(limit).toList();
    }

    public List<Transactions> getTransactionsByAccount(Account account) {
        if (account == null) return Collections.emptyList();
        return transactionRepository.findByAccount(account);
    }

    public Optional<Transactions> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Double getTotalIncomeForMonth(User user, LocalDate date) {
        // Implement logic to calculate total income for the current month
        return 0.0; // Placeholder
    }

    public Double getTotalExpensesForMonth(User user, LocalDate date) {
        // Implement logic to calculate total expenses for the current month
        return 0.0; // Placeholder
    }
}
