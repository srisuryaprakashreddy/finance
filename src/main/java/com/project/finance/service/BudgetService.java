package com.project.finance.service;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import com.project.finance.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserService userService;

    public Budget createOrUpdateBudget(Long userId, double budgetAmount, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserById(userId);

        Budget budget = budgetRepository.findByUserUserId(userId)
                .orElse(new Budget());

        budget.setUser(user);
        budget.setAmount(budgetAmount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        // Reset remainingBudget to full budget amount on update or create
        budget.setRemainingBudget(budgetAmount);

        return budgetRepository.save(budget);
    }

    public Budget getBudgetByUserId(Long userId) {
        return budgetRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Budget not found for user"));
    }

    public Budget updateBudget(Budget budget) {
        return budgetRepository.save(budget);
    }
}
