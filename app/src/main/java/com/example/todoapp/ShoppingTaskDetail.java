package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShoppingTaskDetail extends AppCompatActivity {
    private ShoppingTask shoppingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_task_detail);

        Intent intent = getIntent();
        shoppingTask = (ShoppingTask) intent.getSerializableExtra("task");
        String task = shoppingTask.getTask();
        String description = shoppingTask.getDescription();
        String date = shoppingTask.getDate();
        String productUrl = shoppingTask.getProductUrl();
        String shoppingLocation = shoppingTask.getShoppingLocation();

        TextView tvTask = findViewById(R.id.task);
        TextView tvDescription = findViewById(R.id.description);
        TextView tvDate = findViewById(R.id.date);
        TextView tvProductUrl = findViewById(R.id.productUrl);
        TextView tvShoppingLocation = findViewById(R.id.shoppingLocation);

        Button btnProductUrl = findViewById(R.id.btnProductUrl);
        Button btnShoppingLocation = findViewById(R.id.btnShoppingMap);

        btnProductUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProductUrl();
            }
        });

        btnShoppingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShoppingLocation();
            }
        });

        tvTask.setText(task);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvProductUrl.setText(productUrl);
        tvShoppingLocation.setText(shoppingLocation);
    }

    private void goToShoppingLocation() {
        String sSource = "";
        String sDest = shoppingTask.getShoppingLocation();
        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDest);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //if gg map is not installed
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void goToProductUrl() {
        String url = shoppingTask.getProductUrl();
        Uri productPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, productPage);
        if (intent.resolveActivity(getPackageManager()) != null && url.startsWith("https://")) {
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Product url is not valid!", Toast.LENGTH_SHORT).show();
        }
    }
}