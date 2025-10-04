package com.rit.expencetrack;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Expensedetails extends AppCompatActivity {
    TextView tvcat, tvmonth;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expensedetails);

        // Get category and month from intent
        String category = getIntent().getStringExtra("CATEGORY");
        int month = getIntent().getIntExtra("MONTH", -1);


        tvcat = findViewById(R.id.valuecat);
        tvmonth = findViewById(R.id.valuebug);

        month=month+1;

        tvcat.setText(category);
        tvmonth.setText(String.valueOf(month));

        db = new DBHelper(this);

        // Fetch expense details based on category and month
        if (category != null && month != -1) {
            List<Expensed> expenseDetails = db.getExpensesByCategoryAndMonth(category, month);

            // Toast message to confirm the number of expense entries fetched

            // Pass the fetched data to populate the table
            populateExpenseTable(expenseDetails);
        } else {
            Toast.makeText(this, "No valid category or month selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to populate the table layout with expenses
    private void populateExpenseTable(List<Expensed> expenses) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Clear any existing rows except the header
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }

        // Iterate over the expense details and add a new row for each
        for (Expensed expense : expenses) {
            TableRow tableRow = new TableRow(this);

            tableRow.setBackgroundResource(R.drawable.reoundtext);

            // Create TextViews for each column: Date, Description, Amount
            TextView dateText = new TextView(this);
            dateText.setText(expense.getDate());  // Date
            dateText.setGravity(Gravity.CENTER);
            dateText.setTextColor(getResources().getColor(R.color.darktxt));
            dateText.setTextSize(17);
            dateText.setPadding(8, 9, 5, 9);

            TextView descriptionText = new TextView(this);
            descriptionText.setText(expense.getDescription());  // Description
            descriptionText.setGravity(Gravity.CENTER);
            descriptionText.setTextColor(getResources().getColor(R.color.darktxt));
            descriptionText.setTextSize(17);
            descriptionText.setPadding(5, 9, 5, 9);

            TextView amountText = new TextView(this);
            amountText.setText(String.valueOf(expense.getAmount()));  // Amount
            amountText.setGravity(Gravity.CENTER);
            amountText.setTextColor(getResources().getColor(R.color.darktxt));
            amountText.setTextSize(17);
             amountText.setPadding(5, 9, 5, 9);

            // Add TextViews to the TableRow
            tableRow.addView(dateText);
            tableRow.addView(descriptionText);
            tableRow.addView(amountText);

            // Add the TableRow to the TableLayout
            tableLayout.addView(tableRow);
        }
    }
}
