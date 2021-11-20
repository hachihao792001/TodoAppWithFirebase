package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShoppingTaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_task_detail);

        Intent intent = getIntent();
        String task = intent.getStringExtra("task");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String productUrl = intent.getStringExtra("productUrl");
        String shoppingLocation = intent.getStringExtra("shoppingLocation");

        TextView tvTask = findViewById(R.id.task);
        TextView tvDescription = findViewById(R.id.description);
        TextView tvDate = findViewById(R.id.date);
        TextView tvProductUrl = findViewById(R.id.productUrl);
        TextView tvShoppingLocation = findViewById(R.id.shoppingLocation);

        tvTask.setText(task);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvProductUrl.setText(productUrl);
        tvShoppingLocation.setText(shoppingLocation);
    }
}