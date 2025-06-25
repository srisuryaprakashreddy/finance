package com.project.finance.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double balance;
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transactions> transactions;

    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Transactions> getTransactions() { return transactions; }
    public void setTransactions(List<Transactions> transactions) { this.transactions = transactions; }
}
