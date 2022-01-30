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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class ShoppingTaskDetail extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCircleClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    private ShoppingTask shoppingTask;
    private GoogleMap mMap;
    private String shoppingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);

        setContentView(R.layout.shopping_task_detail);

        Intent intent = getIntent();
        shoppingTask = (ShoppingTask) intent.getSerializableExtra("task");
        String userId = intent.getStringExtra("userId");

        String task = shoppingTask.getTask();
        String description = shoppingTask.getDescription();
        String date = shoppingTask.getDate();
        String productUrl = shoppingTask.getProductUrl();
        shoppingLocation = shoppingTask.getShoppingLocation();

        ImageView taskImage = findViewById(R.id.taskImage);
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

        Utils.downloadImageFromStorage(userId, shoppingTask.getId(), bitmap1 -> taskImage.setImageBitmap(bitmap1));
        tvTask.setText(task);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvProductUrl.setText(productUrl);
        tvShoppingLocation.setText(shoppingLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        } else {
            Toast.makeText(this, "Product url is not valid!", Toast.LENGTH_SHORT).show();
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

        LatLng latLngPlace = getLocationFromAddress(ShoppingTaskDetail.this, shoppingLocation);

        if (latLngPlace != null) {
            mMap.setOnMarkerClickListener(this);
            mMap.setOnCircleClickListener(this);
            mMap.setOnMapClickListener(this);
            mMap.setOnMapLongClickListener(this);

            MarkerOptions mMarker = new MarkerOptions().position(latLngPlace).title(shoppingLocation);

            mMap.addMarker(mMarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngPlace));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 16));
        } else {
            Toast.makeText(ShoppingTaskDetail.this, "Shopping location is not valid!", Toast.LENGTH_SHORT).show();
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
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }
}