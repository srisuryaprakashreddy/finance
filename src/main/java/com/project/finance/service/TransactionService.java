// service/TransactionService.java
package com.project.finance.service;

import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import com.project.finance.model.Account;
import com.project.finance.repository.TransactionRepository;
import com.project.finance.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transactions saveTransaction(Transactions transaction) {
        Account account = transaction.getAccount();
        if ("INCOME".equals(transaction.getType())) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else if ("EXPENSE".equals(transaction.getType())) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        }
        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    public List<Transactions> getTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public List<Transactions> getTransactionsByAccount(Account account) {
        return transactionRepository.findByAccount(account);
    }

    public Optional<Transactions> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Double getTotalIncome(User user) {
        Double income = transactionRepository.sumByUserAndType(user, "INCOME");
        return income != null ? income : 0.0;
    }

    public Double getTotalExpenses(User user) {
        Double expenses = transactionRepository.sumByUserAndType(user, "EXPENSE");
        return expenses != null ? expenses : 0.0;
    }
}
