package com.project.finance.service;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import com.project.finance.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public List<Budget> getBudgetsByUser(User user) {
        return budgetRepository.findByUser(user);
    }

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget getBudgetByUserAndCategory(User user, String category) {
        return budgetRepository.findByUserAndCategory(user, category);
    }

    /**
     * Update the spent amount for a category's budget.
     * If no budget exists for this category, create one using the transaction's amount and date.
     * Only update spent if the transaction date is within the budget's start and end dates.
     */
    public void updateSpent(User user, String category, Double amount, boolean isDebit, LocalDate transactionDate) {
        Budget budget = getBudgetByUserAndCategory(user, category);
        if (budget == null) {
            // Create a new budget for this category
            Budget newBudget = new Budget();
            newBudget.setUser(user);
            newBudget.setCategory(category);
            newBudget.setAmount(amount); // initial budget amount is the transaction's amount
            newBudget.setSpent(amount);
            newBudget.setStartDate(transactionDate);
            newBudget.setEndDate(transactionDate);
            saveBudget(newBudget);
        } else {
            // Only update if transaction date is within budget period
            if (budget.getStartDate() != null && budget.getEndDate() != null) {
                if (transactionDate.isBefore(budget.getStartDate()) || transactionDate.isAfter(budget.getEndDate())) {
                    return; // Do not update spent if transaction date outside budget period
                }
            }
            if (isDebit) {
                budget.setSpent(budget.getSpent() + amount);
            } else {
                budget.setSpent(Math.max(0, budget.getSpent() - amount));
            }
            saveBudget(budget);
        }
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id).get();
    }
}
