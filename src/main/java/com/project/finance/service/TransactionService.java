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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    public List<Transactions> getRecentTransactions(User user, int limit) {
        return transactionRepository.findByUserOrderByDateDesc(user).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Transactions> getTransactionsByAccount(Account account) {
        return transactionRepository.findByAccountOrderByDateDesc(account);
    }

    public Optional<Transactions> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Double getTotalIncomeForMonth(User user, LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        return getTransactionsByUser(user).stream()
                .filter(t -> "CREDIT".equals(t.getType()) && !t.getDate().isBefore(startOfMonth) && !t.getDate().isAfter(endOfMonth))
                .mapToDouble(Transactions::getAmount)
                .sum();
    }

    public Double getTotalExpensesForMonth(User user, LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        return getTransactionsByUser(user).stream()
                .filter(t -> "DEBIT".equals(t.getType()) && !t.getDate().isBefore(startOfMonth) && !t.getDate().isAfter(endOfMonth))
                .mapToDouble(Transactions::getAmount)
                .sum();
    }
}
