package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MeetingTaskDetail extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCircleClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    MeetingTask thisTask;
    private GoogleMap mMap;
    private String task, description, date, meetingUrl, meetingLocation;

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

        task = thisTask.getTask();
        description = thisTask.getDescription();
        date = thisTask.getDate();
        meetingUrl = thisTask.getMeetingUrl();
        meetingLocation = thisTask.getMeetingLocation();

        taskTextView.setText(task);
        descTextView.setText(description);
        dateTextView.setText(date);
        urlTextView.setText(meetingUrl);
        locationTextView.setText(meetingLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void seeMapOnCLick(View v) {
        String sSource = "";
        String sDest = thisTask.getMeetingLocation();
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

    public void goToMeetingOnClick(View v) {
        String url = thisTask.getMeetingUrl();
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null && url.startsWith("https://")) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Meeting url is not valid!", Toast.LENGTH_SHORT).show();
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
        mMap = googleMap;

        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

        if (currentHourIn24Format >= 6 && currentHourIn24Format <= 17) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstylelight));
        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        }

        LatLng latLngPlace = getLocationFromAddress(MeetingTaskDetail.this, meetingLocation);

        if (latLngPlace != null) {
            mMap.setOnMarkerClickListener(this);
            mMap.setOnCircleClickListener(this);
            mMap.setOnMapClickListener(this);
            mMap.setOnMapLongClickListener(this);

            MarkerOptions mMarker = new MarkerOptions().position(latLngPlace).title(meetingLocation);

            mMap.addMarker(mMarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPlace));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 16));
        }
        else {
            Toast.makeText(MeetingTaskDetail.this, "Meeting location is not valid!", Toast.LENGTH_SHORT).show();
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() == 0) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }
}