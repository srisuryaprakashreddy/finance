package com.project.finance.service;

import com.project.finance.model.Account;
import com.project.finance.model.Budget;
import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import com.project.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BudgetService budgetService;

    /**
     * Process a transaction with budget and account balance checks.
     *
     * @param userId      User ID performing transaction
     * @param amount      Transaction amount
     * @param description Transaction description
     * @param category    Transaction category
     * @param type        Transaction type ("DEBIT" or "CREDIT")
     * @param userConsent User consent for overspending ("yes" or "no")
     * @return message on transaction status
     */
    public String processTransaction(Long userId, double amount, String description,
                                     String category, String type, String userConsent) {
        User user = userService.getUserById(userId);
        Account account = accountService.getAccountByUserId(userId);
        Budget budget = budgetService.getBudgetByUserId(userId);

        if ("DEBIT".equalsIgnoreCase(type)) {
            if (budget.getRemainingBudget() < amount) {
                if (!"yes".equalsIgnoreCase(userConsent)) {
                    return "Warning: Budget exceeded. Please confirm with userConsent=yes";
                }
                if (account.getBalance() < amount) {
                    return "Transaction failed: Insufficient account balance";
                }
                budget.setRemainingBudget(budget.getRemainingBudget() - amount);
                account.setBalance(account.getBalance() - amount);
            } else {
                budget.setRemainingBudget(budget.getRemainingBudget() - amount);
                account.setBalance(account.getBalance() - amount);
            }
        } else if ("CREDIT".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance() + amount);
        } else {
            return "Invalid transaction type";
        }

        accountService.updateAccount(account);
        budgetService.updateBudget(budget);

        Transactions transaction = new Transactions();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.isDebit();
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);

        return "Transaction successful";
    }

    public List<Transactions> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByAccountUserUserId(userId);
    }
}
