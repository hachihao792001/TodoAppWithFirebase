package com.example.todoapp;

public class RelaxingTask extends TaskModel {
    private String playlistName;

    public RelaxingTask() {
        playlistName = "";
    }

    public RelaxingTask(String playlistName) {
        this.playlistName = playlistName;
    }

    public RelaxingTask(String task, String description, String id, String date, TaskType taskType, String playlistName) {
        super(task, description, id, date, taskType);
        this.playlistName = playlistName;
    }

    public String getPlaylistName() {
        return this.playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
