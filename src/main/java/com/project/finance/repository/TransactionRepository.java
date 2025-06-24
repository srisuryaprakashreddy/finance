// repository/TransactionRepository.java
package com.project.finance.repository;

import com.project.finance.model.Transactions;
import com.project.finance.model.User;
import com.project.finance.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByUser(User user);
    List<Transactions> findByAccount(Account account);
    List<Transactions> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    @Query("SELECT SUM(t.amount) FROM Transactions t WHERE t.user = ?1 AND t.type = ?2")
    Double sumByUserAndType(User user, String type);
}
