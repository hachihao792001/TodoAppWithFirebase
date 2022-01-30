package com.example.todoapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.DialogFragment;


public class DrawImageDialog extends DialogFragment {

    //Trả kết quả từ DialogFragment ra bên ngoài: https://stackoverflow.com/a/14808425/13440955
    public interface FinishDrawingListener {
        void onFinishDrawing(Bitmap bitmap);
    }

    private FinishDrawingListener listener;

    public void setOnFinishDrawingListener(FinishDrawingListener listener) {
        this.listener = listener;
    }

    View dialogView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        dialogView = inflater.inflate(R.layout.draw_image, container, false);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawArea drawArea = dialogView.findViewById(R.id.drawArea);
        drawArea.init(metrics);

        Button red = dialogView.findViewById(R.id.redButton);
        Button orange = dialogView.findViewById(R.id.orangeButton);
        Button yellow = dialogView.findViewById(R.id.yellowButton);
        Button green = dialogView.findViewById(R.id.greenButton);
        Button blue = dialogView.findViewById(R.id.blueButton);

        Button eraser = dialogView.findViewById(R.id.eraseButton);
        Button clear = dialogView.findViewById(R.id.clearButton);

        Button cancel = dialogView.findViewById(R.id.cancel);
        Button ok = dialogView.findViewById(R.id.ok);

        red.setOnClickListener(v -> {
            drawArea.setPenColor(getActivity().getResources().getColor(R.color.red));
        });
        orange.setOnClickListener(v -> {
            drawArea.setPenColor(getActivity().getResources().getColor(R.color.orange));
        });
        yellow.setOnClickListener(v -> {
            drawArea.setPenColor(getActivity().getResources().getColor(R.color.yellow));
        });
        green.setOnClickListener(v -> {
            drawArea.setPenColor(getActivity().getResources().getColor(R.color.green));
        });
        blue.setOnClickListener(v -> {
            drawArea.setPenColor(getActivity().getResources().getColor(R.color.blue));
        });
        eraser.setOnClickListener(v -> {
            drawArea.eraser();
        });
        clear.setOnClickListener(v -> {
            drawArea.clear();
        });

        cancel.setOnClickListener(v -> dismiss());
        ok.setOnClickListener(
                v -> {
                    listener.onFinishDrawing(drawArea.getmBitmap());
                    dismiss();
                });

        return dialogView;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

}
