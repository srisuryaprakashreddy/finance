// service/BudgetService.java
package com.project.finance.service;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import com.project.finance.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public Budget getBudgetByUserAndCategory(User user, String category) {
        return budgetRepository.findByUserAndCategory(user, category);
    }
}
