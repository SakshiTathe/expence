package com.rit.expencetrack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private Context context;
    private List<BudgetModel> budgetList;
    private DBHelper dbHelper; // Added reference to DBHelper

    public BudgetAdapter(Context context, List<BudgetModel> budgetList, DBHelper dbHelper) {
        this.context = context;
        this.budgetList = budgetList;
        this.dbHelper = dbHelper; // Initialize DBHelper
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_card, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetModel budgetModel = budgetList.get(position);
        holder.categoryTextView.setText(budgetModel.getCategory());
        holder.budgetTextView.setText(String.valueOf(budgetModel.getBudget()));


        holder.itemView.setOnClickListener(v -> {
            String details = "Category: " + budgetModel.getCategory() + "\n" +
                    "Budget: " + budgetModel.getBudget() + "\n";
            String details2 =
                    "Total Expense: " + budgetModel.getTotalExpense() + "\n" +
                    "Remaining Budget: " + budgetModel.getBudgetMinusExpense();
            String details3 = "CategoryID: " + budgetModel.getId() + "\n" +
                    "month: " + budgetModel.getMonth() + "\n";
            for(int i=0;i<2;i++){
                Toast.makeText(context, details, Toast.LENGTH_LONG).show();
            }
            for(int i=0;i<2;i++){
                Toast.makeText(context, details3, Toast.LENGTH_LONG).show();
            }

        });

        // Handle the delete button click
        holder.deleteButton.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteBudget(budgetModel.getId());
            if (isDeleted) {
                // Remove the item from the list and notify the adapter
                budgetList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Budget deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error deleting budget", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Expense_Add.class);
            intent.putExtra("category", budgetModel.getCategory().toString());
            intent.putExtra("budget", String.valueOf(budgetModel.getBudget()));
            intent.putExtra("categoryId", budgetModel.getId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, budgetTextView;
        ImageButton editButton, deleteButton;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.catalpha);
            budgetTextView = itemView.findViewById(R.id.butrs);
            //editButton = itemView.findViewById(R.id.editbtn);
            deleteButton = itemView.findViewById(R.id.deletebtn);
        }
    }
}
