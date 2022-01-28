package com.example.todoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DrawImageDialog extends AlertDialog {
    Context context;
    View dialogView;

    protected DrawImageDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.draw_image, null);
        setView(dialogView);

        super.onCreate(savedInstanceState);


        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawArea drawArea = findViewById(R.id.drawArea);
        drawArea.init(metrics);

        Button red = findViewById(R.id.redButton);
        Button orange = findViewById(R.id.orangeButton);
        Button yellow = findViewById(R.id.yellowButton);
        Button green = findViewById(R.id.greenButton);
        Button blue = findViewById(R.id.blueButton);

        Button eraser = findViewById(R.id.eraseButton);
        Button clear = findViewById(R.id.clearButton);

        Button cancel = findViewById(R.id.cancel);
        Button ok = findViewById(R.id.ok);

        red.setOnClickListener(v -> {
            drawArea.setPenColor(context.getResources().getColor(R.color.red));
        });
        orange.setOnClickListener(v -> {
            drawArea.setPenColor(context.getResources().getColor(R.color.orange));
        });
        yellow.setOnClickListener(v -> {
            drawArea.setPenColor(context.getResources().getColor(R.color.yellow));
        });
        green.setOnClickListener(v -> {
            drawArea.setPenColor(context.getResources().getColor(R.color.green));
        });
        blue.setOnClickListener(v -> {
            drawArea.setPenColor(context.getResources().getColor(R.color.blue));
        });
        eraser.setOnClickListener(v -> {
            drawArea.eraser();
        });
        clear.setOnClickListener(v -> {
            drawArea.clear();
        });

        cancel.setOnClickListener(v -> dismiss());
        ok.setOnClickListener(v -> dismiss());
    }
}
