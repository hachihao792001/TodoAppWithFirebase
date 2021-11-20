package com.example.todoapp;

public class MeetingTask extends TaskModel {
    private String meetingUrl, meetingLocation;

    public MeetingTask() {
    }

    public MeetingTask(String meetingUrl, String meetingLocation) {
        this.meetingUrl = meetingUrl;
        this.meetingLocation = meetingLocation;
    }

    public MeetingTask(String task, String description, String id, String date, TaskType taskType, String meetingUrl, String meetingLocation) {
        super(task, description, id, date, taskType);
        this.meetingUrl = meetingUrl;
        this.meetingLocation = meetingLocation;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }
}
