package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText regEmail, regPwd,regConPwd;
    private Button regBtn;
    private TextView regQn;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

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
        regConPwd=findViewById(R.id.registrationConfirmPassword);
        regBtn = findViewById(R.id.registrationButton);
        regQn = findViewById(R.id.registrationQuestion);

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        regQn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = regEmail.getText().toString().trim();
                String password = regPwd.getText().toString().trim();
                String passwordCon = regConPwd.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    regEmail.setError("Email is required!");
                    return;
                }
                else if (TextUtils.isEmpty(password)){
                    regPwd.setError("Password is required!");
                    return;
                }else if (TextUtils.isEmpty(passwordCon)){
                    regConPwd.setError("Password is required!");
                    return;
                }else if (!password.equals(passwordCon)){
                    regConPwd.setError("Confirmed password is incorrect!");
                    return;
                }

                else {
       //             loader.setMessage("Registration in progress...");

                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                String error = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Registration failed! Please try again " + error, Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();
                        }
                    });
                }

            }
        });
    }

}