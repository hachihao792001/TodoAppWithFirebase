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

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText loginEmail, loginPwd;
    private Button loginBtn;
    private TextView loginQn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        loginEmail = findViewById(R.id.loginEmail);
        loginPwd = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        loginQn = findViewById(R.id.loginQuestion);

        loginQn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}