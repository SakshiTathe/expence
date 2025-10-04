package com.rit.expencetrack;

public class Expensed{
    private String date;
    private String description;
    private double amount;

    public Expensed(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // Getters for each field
    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

}

