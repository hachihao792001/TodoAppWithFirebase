package com.example.todoapp;

public class RelaxingTask {
    private String playlistName;

    public RelaxingTask(){
        playlistName="";
    }
    public RelaxingTask(String playlistName){
        this.playlistName=playlistName;
    }

    public String getPlaylistName(){
        return this.playlistName;
    }

    public void setPlaylistName(String playlistName){
        this.playlistName=playlistName;
    }
}
