package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MeetingTaskDetail extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCircleClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    MeetingTask thisTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_task_detail);

        Intent intent = getIntent();
        thisTask = (MeetingTask) intent.getSerializableExtra("task");

        TextView taskTextView = findViewById(R.id.task);
        TextView descTextView = findViewById(R.id.description);
        TextView dateTextView = findViewById(R.id.date);
        TextView urlTextView = findViewById(R.id.meetingUrl);
        TextView locationTextView = findViewById(R.id.meetingLocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.meetingMap);

        taskTextView.setText(thisTask.getTask());
        descTextView.setText(thisTask.getDescription());
        dateTextView.setText(thisTask.getDate());
        urlTextView.setText(thisTask.getMeetingUrl());
        locationTextView.setText(thisTask.getMeetingLocation());
    }

    public void seeMapOnCLick(View view) {
        String location = thisTask.getMeetingLocation();
    }

    public void goToMeetingOnClick(View view) {
        String url = thisTask.getMeetingUrl();
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onCircleClick(@NonNull Circle circle) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
    }
}