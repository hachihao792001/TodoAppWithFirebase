package com.example.todoapp;

public class RelaxingTask extends TaskModel {
    private String songName;

    public RelaxingTask() {
        songName = "";
    }

    public RelaxingTask(String playlistName) {
        this.songName = playlistName;
    }

    public RelaxingTask(String task, String description, String id, String date, TaskType taskType, String songName) {
        super(task, description, id, date, taskType);
        this.songName = songName;
    }

    public String getPlaylistName() {
        return this.songName;
    }

    public void setPlaylistName(String playlistName) {
        this.songName = playlistName;
    }
}
