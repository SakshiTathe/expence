package com.rit.expencetrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BudgetDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Budget";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_BUDGET = "budget";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_TOTAL_EXPENSE = "total_expense";
    public static final String COLUMN_BUDGET_MINUS_EXPENSE = "budget_minus_expense";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Budget table
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_BUDGET + " REAL, " +
                COLUMN_MONTH + " INTEGER, " +
                COLUMN_TOTAL_EXPENSE + " REAL, " +
                COLUMN_BUDGET_MINUS_EXPENSE + " REAL)";
        db.execSQL(createTable);

        // Create the expenseDetails table
        String createExpenseTable = "CREATE TABLE expenseDetails ("
                + "expense_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "amount REAL, "
                + "date TEXT, "
                + "month INTEGER, "
                + "description TEXT, "
                + "category_id INTEGER, "
                + "categorynames TEXT, "
                + "FOREIGN KEY(category_id) REFERENCES Budget(id))";
        db.execSQL(createExpenseTable);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new budget
    public boolean insertBudget(BudgetModel budgetModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CATEGORY, budgetModel.getCategory());
        contentValues.put(COLUMN_BUDGET, budgetModel.getBudget());
        contentValues.put(COLUMN_MONTH, budgetModel.getMonth());
        contentValues.put(COLUMN_TOTAL_EXPENSE, budgetModel.getTotalExpense());
        contentValues.put(COLUMN_BUDGET_MINUS_EXPENSE, budgetModel.getBudgetMinusExpense());

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // Get all budgets
    public Cursor getAllBudgets() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Delete a budget by ID
    public boolean deleteBudget(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Add a new expense
    public void addExpense(double amount, String date, int month, String description, String catname, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("date", date);
        values.put("month", month);
        values.put("description", description);
        values.put("category_id", categoryId);
        values.put("categorynames", catname);

        // Insert the new expense
        db.insert("expenseDetails", null, values);

        // Update total expense and remaining budget
        updateBudgetAfterExpense(categoryId, month);
    }

    // Update the Budget table after an expense is added
    // Method to update total expense and budget minus expense after retrieving data from expenseDetails
    public void updateBudgetAfterExpense(int categoryId, int month) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Step 1: Retrieve all expenses for the given category and month from expenseDetails
        Cursor expenseCursor = db.rawQuery("SELECT SUM(amount) FROM expenseDetails WHERE category_id = ? AND month = ?",
                new String[]{String.valueOf(categoryId), String.valueOf(month)});

        if (expenseCursor.moveToFirst()) {
            // Step 2: Get the total amount of expenses for the specified category and month
            double totalExpenses = expenseCursor.getDouble(0);

            // Step 3: Retrieve the current budget and total_expense from the Budget table
            Cursor budgetCursor = db.rawQuery("SELECT total_expense, budget FROM Budget WHERE id = ? AND month = ?",
                    new String[]{String.valueOf(categoryId), String.valueOf(month)});

            if (budgetCursor.moveToFirst()) {
                // Get the current total expense and budget
                double currentTotalExpense = budgetCursor.getDouble(0);
                double budget = budgetCursor.getDouble(1);

                // Step 4: Update the total expense and budget_minus_expense in the Budget table
                double newTotalExpense = currentTotalExpense + totalExpenses;
                double budgetMinusExpense = budget - newTotalExpense;

                // Prepare the updated values
                ContentValues values = new ContentValues();
                values.put(COLUMN_TOTAL_EXPENSE, newTotalExpense);
                values.put(COLUMN_BUDGET_MINUS_EXPENSE, budgetMinusExpense);

                // Step 5: Update the Budget table with the new total expense and budget_minus_expense
                db.update(TABLE_NAME, values, "id = ? AND month = ?",
                        new String[]{String.valueOf(categoryId), String.valueOf(month)});
            }
            budgetCursor.close();
        }
        expenseCursor.close();
    }

    // Get expenses by month
    public List<Expense> getExpensesByMonthAndYear(int month, int year) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MONTH + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(month)});

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUDGET)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_EXPENSE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_MINUS_EXPENSE))
                );
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenseList;
    }


    // Add this method in your DBHelper class
    public List<Expensed> getExpensesByCategoryAndMonth(String category, int month) {
        List<Expensed> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get the expenses based on category and month
        String query = "SELECT date, description, amount FROM expenseDetails " +
                "WHERE categorynames = ? AND month = ?";
        Cursor cursor = db.rawQuery(query, new String[]{category, String.valueOf(month)});

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                Log.d("ExpenseDetails", "Date: " + date + ", Description: " + description + ", Amount: " + amount);
                Expensed expense = new Expensed(date, description, amount);

                expenseList.add(expense);
                // Add each expense entry to the list
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenseList;
    }
}
