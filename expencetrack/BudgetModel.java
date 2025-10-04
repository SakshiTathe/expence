package com.rit.expencetrack;

public class BudgetModel {
    private int id;
    private String category;
    private double budget;
    private int month;
    private double totalExpense;
    private double budgetMinusExpense;


    public BudgetModel(int id, String category, double budget, int month, double totalExpense, double budgetMinusExpense) {
        this.id = id;
        this.category = category;
        this.budget = budget;
        this.month = month;
        this.totalExpense = totalExpense;
        this.budgetMinusExpense = budgetMinusExpense;
    }

    // Getters and setters for each field
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }

    public double getBudgetMinusExpense() { return budgetMinusExpense; }
    public void setBudgetMinusExpense(double budgetMinusExpense) { this.budgetMinusExpense = budgetMinusExpense; }
}

