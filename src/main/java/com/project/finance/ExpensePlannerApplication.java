package com.project.finance;


import com.project.finance.model.Account;
import com.project.finance.model.Budget;
import com.project.finance.model.Transactions;
import com.project.finance.service.AccountService;
import com.project.finance.service.BudgetService;
import com.project.finance.service.TransactionService;
import com.project.finance.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class ExpensePlannerApplication {
	public static void main(String[] args) {
		 SpringApplication.run(ExpensePlannerApplication.class, args);
	}
}