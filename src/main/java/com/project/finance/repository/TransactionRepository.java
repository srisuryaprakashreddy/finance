package com.project.finance.repository;

import com.project.finance.model.Account;
import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByUser(User user);
    List<Transactions> findByAccount(Account account);

    @Query("SELECT SUM(t.amount) FROM Transactions t WHERE t.user = :user AND t.category = :category AND t.type = 'DEBIT' AND t.date BETWEEN :startDate AND :endDate")
    Double sumDebitsForBudget(
            @Param("user") User user,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
