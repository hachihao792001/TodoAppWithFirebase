package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 12);
        else {
            callPhoneIntent();
        }
    }

    void callPhoneIntent() {
        String phoneNumber = thisTask.getPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void emailOnClick(View view) {
        String email = thisTask.getEmail();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactTaskDetail.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 12) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                callPhoneIntent();
            }
        }
    }
}