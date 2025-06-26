package com.project.finance.controller;

import com.project.finance.model.Budget;
import com.project.finance.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    // List all budgets
    @GetMapping
    public String listBudgets(Model model) {
        List<Budget> budgets = budgetRepository.findAll();
        model.addAttribute("budgets", budgets);
        model.addAttribute("totalSpent", budgets.stream().mapToDouble(Budget::getSpent).sum());
        model.addAttribute("totalBudget", budgets.stream().mapToDouble(Budget::getAmount).sum());
        return "budget";
    }

    // Show form to add a new budget
    @GetMapping("/add")
    public String showAddBudgetForm(Model model) {
        model.addAttribute("budget", new Budget());
        return "budget";
    }

    // Add a new budget
    @PostMapping("/add")
    public String addBudget(@ModelAttribute Budget budget) {
        budgetRepository.save(budget);
        return "redirect:/budget";
    }

    // Show form to update a budget
    @GetMapping("/edit/{id}")
    public String showEditBudgetForm(@PathVariable Long id, Model model) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid budget Id:" + id));
        model.addAttribute("budget", budget);
        return "budget";
    }

    // Update budget
    @PostMapping("/edit/{id}")
    public String updateBudget(@PathVariable Long id, @ModelAttribute Budget budget) {
        budget.setId(id);
        budgetRepository.save(budget);
        return "redirect:/budget";
    }

    // Delete budget
    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        budgetRepository.deleteById(id);
        return "redirect:/budget";
    }
}
