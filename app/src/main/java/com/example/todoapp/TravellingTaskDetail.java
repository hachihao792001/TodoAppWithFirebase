package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TravellingTaskDetail extends AppCompatActivity {
    private TravellingTask travellingTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelling_task_detail);

        Intent intent = getIntent();
        travellingTask = (TravellingTask) intent.getSerializableExtra("task");
        String task = travellingTask.getTask();
        String description = travellingTask.getDescription();
        String date = travellingTask.getDate();
        String place = travellingTask.getPlace();

        TextView tvTask = findViewById(R.id.task);
        TextView tvDescription = findViewById(R.id.description);
        TextView tvDate = findViewById(R.id.date);
        TextView tvPlace = findViewById(R.id.place);

        Button btnTravellingMap = findViewById(R.id.btnTravellingMap);

        btnTravellingMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTravellingLocation();
            }
        });

        tvTask.setText(task);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvPlace.setText(place);
    }

    private void goToTravellingLocation() {
        String sSource = "";
        String sDest = travellingTask.getPlace();
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
}