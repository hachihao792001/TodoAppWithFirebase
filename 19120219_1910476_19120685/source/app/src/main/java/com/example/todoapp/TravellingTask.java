package com.example.todoapp;

public class TravellingTask extends TaskModel{
    private String place;

    public TravellingTask() {

    }

    public TravellingTask(String place) {
        this.place = place;
    }

    public TravellingTask(String task, String description, String id, String date, TaskType taskType, String place) {
        super(task, description, id, date, taskType);
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
