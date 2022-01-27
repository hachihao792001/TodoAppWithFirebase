package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

public class OfficeTaskDetail extends AppCompatActivity {
        OfficeTask thisTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.office_task_detail);
        Intent intent = getIntent();
        thisTask = (OfficeTask) intent.getSerializableExtra("task");
        TextView taskTextView = findViewById(R.id.task);
        TextView descTextView = findViewById(R.id.description);
        TextView dateTextView = findViewById(R.id.date);
        taskTextView.setText(thisTask.getTask());
        descTextView.setText(thisTask.getDescription());
        dateTextView.setText(thisTask.getDate());

    }

    public void chooseFile(View view){
        String sPath= Environment.getExternalStorageDirectory()+"/";
        Uri uri=Uri.parse(sPath);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(uri,"*/*");
        startActivity(intent);
    }
}