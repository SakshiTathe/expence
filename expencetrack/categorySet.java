package com.rit.expencetrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class categorySet extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private List<BudgetModel> budgetList;
    private DBHelper dbHelper;
    ImageButton expact,home,budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_set);

        recyclerView = findViewById(R.id.recylecat);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns
        dbHelper = new DBHelper(this);

        loadData(); // Load data from database and populate RecyclerView
        expact=findViewById(R.id.expense);
        home= findViewById(R.id.home);
        budget= findViewById(R.id.budget);
        // Button to open dialog for adding new budget entry
        expact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Expense_Budget.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), categorySet.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.diag).setOnClickListener(v -> openDialog()); // Passing null for new entry
    }

    // Method to load data from the database
    private void loadData() {
        Cursor cursor = dbHelper.getAllBudgets();
        budgetList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String category = cursor.getString(1);
                double budget = cursor.getDouble(2);
                int month = cursor.getInt(3);
                double totalExpense = cursor.getDouble(4);
                double budgetMinusExpense = cursor.getDouble(5);

                BudgetModel budgetModel = new BudgetModel(id, category, budget, month, totalExpense, budgetMinusExpense);
                budgetList.add(budgetModel);
            } while (cursor.moveToNext());
        }

        cursor.close();

        if (budgetAdapter == null) {
            budgetAdapter = new BudgetAdapter(this, budgetList, dbHelper); // Pass DBHelper to adapter
            recyclerView.setAdapter(budgetAdapter);
        } else {
            budgetAdapter.notifyDataSetChanged();
        }
    }

    // Method to open the dialog to add/update a budget entry
    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(categorySet.this);
        View dialogView = LayoutInflater.from(categorySet.this).inflate(R.layout.dialogueform, null);
        builder.setView(dialogView);

        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        EditText etBudget = dialogView.findViewById(R.id.etBudget);

        builder.setTitle("Add Data")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = etCategory.getText().toString();
                        try {
                            double budget = Double.parseDouble(etBudget.getText().toString());
                            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

                            // Insert new budget entry
                            BudgetModel newModel = new BudgetModel(0, category, budget, currentMonth, 0.0, budget);
                            boolean isInserted = dbHelper.insertBudget(newModel);
                            if (isInserted) {
                                loadData();
                                Toast.makeText(categorySet.this, "Data added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(categorySet.this, "Error adding data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(categorySet.this, "Invalid budget input", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to delete a budget entry

    private void deleteBudget(BudgetModel model, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBudget(model.getId());
                    budgetList.remove(position);
                    budgetAdapter.notifyItemRemoved(position);
                    Toast.makeText(categorySet.this, "Data deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }
}
