package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactTaskDetail extends AppCompatActivity {
    ContactTask thisTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_task_detail);

        Intent intent = getIntent();
        thisTask = (ContactTask) intent.getSerializableExtra("task");

        TextView taskTextView = findViewById(R.id.task);
        TextView descTextView = findViewById(R.id.description);
        TextView dateTextView = findViewById(R.id.date);
        TextView phoneNumberTextView = findViewById(R.id.phoneNumberText);
        TextView emailTextView = findViewById(R.id.emailText);

        taskTextView.setText(thisTask.getTask());
        descTextView.setText(thisTask.getDescription());
        dateTextView.setText(thisTask.getDate());
        phoneNumberTextView.setText(thisTask.getPhoneNumber());
        emailTextView.setText(thisTask.getEmail());
    }

    public void callOnClick(View view) {
        String phone = thisTask.getPhoneNumber();
        //TODO call intent
    }

    public void emailOnClick(View view) {
        String email = thisTask.getEmail();
        //TODO call intent
    }
}