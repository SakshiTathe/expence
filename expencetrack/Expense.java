package com.rit.expencetrack;

public class Expense {
    private String category;
    private double budget;
    private double expense;
    private double remain;

    public Expense(String category, double budget, double expense, double remain) {
        this.category = category;
        this.budget = budget;
        this.expense = expense;
        this.remain = remain;
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public double getBudget() {
        return budget;
    }

    public double getExpense() {
        return expense;
    }

    public double getRemain() {
        return remain;
    }
}

