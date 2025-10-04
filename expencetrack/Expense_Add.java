package com.rit.expencetrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Expense_Add extends AppCompatActivity {

    private int selectedMonth = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expense_add);

        Button saveBtn = findViewById(R.id.saveBtn);
        TextView txtcat= findViewById(R.id.valuecat);
        TextView txtBug= findViewById(R.id.valuebug);

        Intent intent= getIntent();
        String j= intent.getExtras().getString("category");
        txtcat.setText(j);

        String k= intent.getExtras().getString("budget");
        txtBug.setText(k);


        saveBtn.setOnClickListener(v -> {
            String amountStr = ((EditText) findViewById(R.id.amount)).getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String date = ((TextView) findViewById(R.id.date)).getText().toString();
            if (date.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            String description = ((EditText) findViewById(R.id.note)).getText().toString();
            int categoryId = getIntent().getIntExtra("categoryId", -1);
            String categoryn = getIntent().getStringExtra("category");

            if (categoryId == -1) {
                Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the current month (for budget tracking)
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;

            // Insert expense into database
            DBHelper db = new DBHelper(this);
            db.addExpense(amount, date, selectedMonth, description,categoryn, categoryId);

            Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity after saving
        });

    }
    // In your Expense_Add Activity
    public void opencal(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year1, monthOfYear, dayOfMonth) -> {
                    // Set selected date on the TextView
                    TextView dateTextView = findViewById(R.id.date);
                    dateTextView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                    selectedMonth = monthOfYear + 1;
                }, year, month, day);
        datePickerDialog.show();
    }

}