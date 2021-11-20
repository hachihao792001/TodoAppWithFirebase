package com.example.todoapp;

public class OfficeTask extends TaskModel {
    private String fileName;

    public OfficeTask() {
        fileName = "";
    }

    public OfficeTask(String fileName) {
        this.fileName = fileName;
    }

    public OfficeTask(String task, String description, String id, String date, TaskType taskType, String fileName) {
        super(task, description, id, date, taskType);
        this.fileName = fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
