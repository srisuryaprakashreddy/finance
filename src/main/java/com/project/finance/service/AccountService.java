package com.project.finance.service;

import com.project.finance.model.Account;
import com.project.finance.model.User;
import com.project.finance.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    public Account getAccountByUserId(Long userId) {
        return accountRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found for user"));
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account createAccountForUser(Long userId, double initialBalance) {
        User user = userService.getUserById(userId);
        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance);
        return accountRepository.save(account);
    }
}
