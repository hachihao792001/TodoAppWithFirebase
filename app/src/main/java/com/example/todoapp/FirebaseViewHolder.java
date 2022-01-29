package com.example.todoapp;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setImage(Bitmap bitmap) {
        ImageView taskImage = mView.findViewById(R.id.taskImage);
        taskImage.setImageBitmap(bitmap);
    }

    public void setTask(String task) {
        TextView taskTextView = mView.findViewById(R.id.taskTv);
        taskTextView.setText(task);
    }

    public void setDescription(String des) {
        TextView desTextView = mView.findViewById(R.id.descriptionTv);
        desTextView.setText(des);
    }

    public void setDate(String date) {
        TextView dateTextView = mView.findViewById(R.id.dateTv);
        dateTextView.setText(date);
    }

    public void setTaskType(TaskType taskType) {
        ImageView icon = mView.findViewById(R.id.taskTypeIcon);
        icon.setImageResource(taskType.iconResource);
    }

    public void setIsDone(boolean done) {
        CheckBox doneCheckBox = mView.findViewById(R.id.doneCheckBox);
        if (doneCheckBox.isChecked() != done)
            doneCheckBox.setChecked(done);
    }
}
