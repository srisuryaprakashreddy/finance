package com.project.finance.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
public class Budget {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private Double amount;
    @Transient // Not stored in DB; calculated on the fly
    private Double spent = 0.0;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getSpent() { return spent; }
    public void setSpent(Double spent) { this.spent = spent; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
