package com.project.finance.service;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import com.project.finance.repository.BudgetRepository;
import com.project.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Budget> getBudgetsByUser(User user) {
        List<Budget> budgets = budgetRepository.findByUser(user);
        // For each budget, dynamically calculate the spent amount from transactions.
        budgets.forEach(budget -> {
            Double spent = transactionRepository.sumDebitsForBudget(
                    user, budget.getCategory(), budget.getStartDate(), budget.getEndDate());
            budget.setSpent(spent != null ? spent : 0.0);
        });
        return budgets;
    }

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}
