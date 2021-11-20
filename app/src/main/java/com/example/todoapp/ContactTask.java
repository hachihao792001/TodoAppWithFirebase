package com.example.todoapp;

public class ContactTask extends TaskModel {
    private String phoneNumber, email;

    public ContactTask() {
    }

    public ContactTask(String task, String description, String id, String date, TaskType taskType, String phoneNumber, String email) {
        super(task, description, id, date, taskType);
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
