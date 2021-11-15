package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class RegistrationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText regEmail, regPwd;
    private Button regBtn;
    private TextView regQn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        toolbar = findViewById(R.id.registrationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");

        regEmail = findViewById(R.id.registrationEmail);
        regPwd = findViewById(R.id.registrationPassword);
        regBtn = findViewById(R.id.registrationButton);
        regQn = findViewById(R.id.registrationQuestion);

        regQn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}