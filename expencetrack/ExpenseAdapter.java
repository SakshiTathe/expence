package com.rit.expencetrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList; // List to store expenses from database
    private Context context;
    private int month;

    public ExpenseAdapter(Context context, List<Expense> expenseList,int month) {
        this.context = context;
        this.expenseList = expenseList;
        this.month = month;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_card, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        // Bind data to the views
        holder.categoryTextView.setText(expense.getCategory());
        holder.budgetTextView.setText(String.valueOf(expense.getBudget()));
        holder.expenseTextView.setText(String.valueOf(expense.getExpense()));
        holder.remainTextView.setText(String.valueOf(expense.getRemain()));
        /*
        holder.viewButton.setOnClickListener(v -> {
            // Show toast with expense details
            Toast.makeText(context, "Category: " + expense.getCategory() +
                    "\nMonth: " + month, Toast.LENGTH_LONG).show();
        });
        */
        // Handle button click for "View"
        holder.viewButton.setOnClickListener(v -> {
            // Show toast with expense details
            Intent intent = new Intent(context,Expensedetails.class);
            intent.putExtra("CATEGORY", expense.getCategory());
            intent.putExtra("MONTH", month); // Pass the fetched month
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // ViewHolder class
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, budgetTextView, expenseTextView, remainTextView;
        Button viewButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.transactionCategory);
            budgetTextView = itemView.findViewById(R.id.budval);
            expenseTextView = itemView.findViewById(R.id.expval);
            remainTextView = itemView.findViewById(R.id.remainval);
            viewButton = itemView.findViewById(R.id.view);

        }
    }
    public void updateExpenseList(List<Expense> newList) {
        this.expenseList.clear();
        this.expenseList.addAll(newList);
        //expenseList = newList;
        notifyDataSetChanged();
    }
}

