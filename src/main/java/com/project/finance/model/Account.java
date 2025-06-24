package com.project.finance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountName;
    private double balance;

    @ManyToOne
    private User user;  // Assuming one User can have multiple accounts

    public Account() {
    }

    public Account(String accountName, double balance, User user) {
        this.accountName = accountName;
        this.balance = balance;
        this.user = user;
    }

    public Account(Long id, String accountName, double balance, User user) {
        this.id = id;
        this.accountName = accountName;
        this.balance = balance;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
