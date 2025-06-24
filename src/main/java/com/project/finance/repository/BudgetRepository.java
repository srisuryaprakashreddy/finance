// repository/BudgetRepository.java
package com.project.finance.repository;

import com.project.finance.model.Budget;
import com.project.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserId(Long userId);
    Budget findByUserAndCategory(User user, String category);
}
