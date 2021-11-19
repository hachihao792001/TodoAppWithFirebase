package com.example.todoapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

class TaskType {
    public String name;
    public int iconResource;

    public TaskType(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }
}

public class TaskTypeAdapter implements SpinnerAdapter {

    public Context context;
    public ArrayList<TaskType> taskTypeList;

    TaskTypeAdapter(Context context, ArrayList<TaskType> taskTypes) {
        this.context = context;
        taskTypeList = taskTypes;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        TaskType thisTaskType = taskTypeList.get(i);

        View v = LayoutInflater.from(context).inflate(R.layout.task_type_dropdownitem, viewGroup, false);
        TextView nameTextView = v.findViewById(R.id.name);
        ImageView iconImageView = v.findViewById(R.id.icon);

        nameTextView.setText(thisTaskType.name);
        iconImageView.setImageResource(thisTaskType.iconResource);

        return v;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TaskType thisTaskType = taskTypeList.get(i);

        View v = LayoutInflater.from(context).inflate(R.layout.task_type_dropdownitem, viewGroup, false);
        TextView nameTextView = v.findViewById(R.id.name);
        ImageView iconImageView = v.findViewById(R.id.icon);

        nameTextView.setText(thisTaskType.name);
        iconImageView.setImageResource(thisTaskType.iconResource);

        return v;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return taskTypeList.size();
    }

    @Override
    public Object getItem(int i) {
        return taskTypeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return taskTypeList.isEmpty();
    }
}
