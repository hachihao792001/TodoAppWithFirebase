package com.example.todoapp;

public class OfficeTask extends TaskModel {

    public OfficeTask() {

    }


    public OfficeTask(String task, String description, String id, String date, TaskType taskType) {
        super(task, description, id, date, taskType);
    }

}
