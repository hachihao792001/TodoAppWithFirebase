package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MeetingTaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_task_detail);

        Intent intent = getIntent();
        String task = intent.getStringExtra("task");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String meetingUrl = intent.getStringExtra("meetingUrl");
        String meetingLocation = intent.getStringExtra("meetingLocation");

        TextView tvTask = findViewById(R.id.task);
        TextView tvDescription = findViewById(R.id.description);
        TextView tvDate = findViewById(R.id.date);
        TextView tvMeetingUrl = findViewById(R.id.meetingUrl);
        TextView tvMeetingLocation = findViewById(R.id.meetingLocation);

        tvTask.setText(task);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvMeetingUrl.setText(meetingUrl);
        tvMeetingLocation.setText(meetingLocation);
    }
}