package com.rit.expencetrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button nextcatInt;
    ImageButton expact,budget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        nextcatInt=findViewById(R.id.setcat);
        expact= findViewById(R.id.expense);
        budget= findViewById(R.id.budget);

        nextcatInt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), categorySet.class);
                startActivity(intent);
            }
        });

        expact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Expense_Budget.class);
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

    }
}