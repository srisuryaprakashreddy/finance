package com.project.finance.repository;

import com.project.finance.model.Account;
import com.project.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
