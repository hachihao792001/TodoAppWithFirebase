package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class ChangeTheme  extends AppCompatActivity implements View.OnClickListener {
    public final static int THEME_RED = 0;
    public final static int THEME_ORANGE = 1;
    public final static int THEME_YELLOW = 2;
    public final static int THEME_GREEN = 3;
    public final static int THEME_BLUE = 4;
    public final static int THEME_PURPLE = 5;
    public final static int THEME_PINK = 6;
    public final static int THEME_GRAY = 7;
    Button back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_change_theme);
        findViewById(R.id.red).setOnClickListener(this);
        findViewById(R.id.orange).setOnClickListener(this);
        findViewById(R.id.yellow).setOnClickListener(this);
        findViewById(R.id.green).setOnClickListener(this);
        findViewById(R.id.blue).setOnClickListener(this);
        findViewById(R.id.purple).setOnClickListener(this);
        findViewById(R.id.pink).setOnClickListener(this);
        findViewById(R.id.gray).setOnClickListener(this);


        back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home=new Intent(ChangeTheme.this, HomeActivity.class);
                startActivity(home);
            }
        });
    }
    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {

            case R.id.red:
                Constant.theme=R.style.AppTheme_Red;
                Utils.changeToTheme(ChangeTheme.this, THEME_RED);
                break;
            case R.id.orange:
                Constant.theme=R.style.AppTheme_Orange;
                Utils.changeToTheme(ChangeTheme.this, THEME_ORANGE);
                break;
            case R.id.yellow:
                Constant.theme=R.style.AppTheme_Yellow;
                Utils.changeToTheme(ChangeTheme.this, THEME_YELLOW);
                break;
            case R.id.green:
                Constant.theme=R.style.AppTheme_Green;
                Utils.changeToTheme(ChangeTheme.this, THEME_GREEN);
                break;
            case R.id.blue:
                Constant.theme=R.style.AppTheme_Blue;
                Utils.changeToTheme(ChangeTheme.this, THEME_BLUE);
                break;
            case R.id.purple:
                Constant.theme=R.style.AppTheme_Purple;
                Utils.changeToTheme(ChangeTheme.this, THEME_PURPLE);
                break;
            case R.id.pink:
                Constant.theme=R.style.AppTheme_Pink;
                Utils.changeToTheme(ChangeTheme.this, THEME_PINK);
                break;
            case R.id.gray:
                Constant.theme=R.style.AppTheme_Gray;
                Utils.changeToTheme(ChangeTheme.this, THEME_GRAY);
                break;
        }
        setTheme(Constant.theme);
        recreateActivity();
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


}
