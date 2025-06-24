// service/AccountService.java
package com.project.finance.service;

import com.project.finance.model.Account;
import com.project.finance.model.User;
import com.project.finance.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public Double getTotalBalance(User user) {
        return getAccountsByUser(user).stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }
}
