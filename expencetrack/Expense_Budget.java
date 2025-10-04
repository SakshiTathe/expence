package com.rit.expencetrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.text.DateFormatSymbols;


import java.util.List;

public class Expense_Budget extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private DBHelper dbHelper;
    private TextView monthTextView;
    private int selectedMonth;  // Track the selected month
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense_budget);
        recyclerView = findViewById(R.id.exprecyclerView);
        monthTextView = findViewById(R.id.mnths);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DBHelper(this);

        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH);  // Starts from 0 (January)
        selectedYear = calendar.get(Calendar.YEAR);
        updateMonthText();


        expenseList = dbHelper.getExpensesByMonthAndYear(selectedMonth,selectedYear);

        // Set up the adapter
        expenseAdapter = new ExpenseAdapter(this, expenseList,selectedMonth);
        recyclerView.setAdapter(expenseAdapter);

        // Next/Previous month buttons
        ImageView back = findViewById(R.id.back);
        ImageView front = findViewById(R.id.front);

        back.setOnClickListener(v -> {
            if (selectedMonth == 0) {
                selectedMonth = 11;  // Move to December
                selectedYear--;      // Move to the previous year
            } else {
                selectedMonth--;
            }
            updateMonthExpenses();
            updateMonthText();  // Update the month display
        });
        front.setOnClickListener(v -> {
            if (selectedMonth == 11) {
                selectedMonth = 0;  // Move to January
                selectedYear++;     // Move to the next year
            } else {
                selectedMonth++;
            }
            updateMonthExpenses();
            updateMonthText();  // Update the month display
        });
        findViewById(R.id.expense2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Expense_Budget.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.home2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.budget2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), categorySet.class);
                startActivity(intent);
            }
        });
    }
    private void updateMonthText() {
        String monthName = new DateFormatSymbols().getMonths()[selectedMonth];
        String monthYearText = monthName + ", " + selectedYear;
        monthTextView.setText(monthYearText);
    }
    private void updateMonthExpenses() {
        List<Expense> updatedList = dbHelper.getExpensesByMonthAndYear(selectedMonth+1,selectedYear);
        double totalExpense = 0;
        double totalBudget = 0;

        for (Expense expense : updatedList) {
            totalExpense += expense.getExpense();
            totalBudget += expense.getBudget();
        }
        TextView topexp = findViewById(R.id.allexp);
        TextView topbug = findViewById(R.id.allbug);
        TextView topremain = findViewById(R.id.allremain);

        topexp.setText(String.valueOf(totalExpense));
        topbug.setText(String.valueOf(totalBudget));

        // Calculate the remaining amount (budget minus expense)
        double remaining = totalBudget - totalExpense;
        topremain.setText(String.valueOf(remaining));
        expenseAdapter.updateExpenseList(updatedList);
    }

}