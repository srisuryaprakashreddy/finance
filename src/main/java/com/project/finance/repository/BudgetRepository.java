package com.project.finance.repository;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    java.util.List<Budget> findByUser(User user);
    Budget findByUserAndCategory(User user, String category);
}
