package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

public class RelaxingTaskDetail extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    RelaxingTask thisTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relaxing_task_detail);
        Intent intent = getIntent();
        thisTask = (RelaxingTask) intent.getSerializableExtra("task");
        TextView taskTextView = findViewById(R.id.task);
        TextView descTextView = findViewById(R.id.description);
        TextView dateTextView = findViewById(R.id.date);
        TextView songName= findViewById(R.id.songName);
        taskTextView.setText(thisTask.getTask());
        descTextView.setText(thisTask.getDescription());
        dateTextView.setText(thisTask.getDate());
        songName.setText(thisTask.getPlaylistName());
    }
    public void onMusicStart(View view){
       mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
       mediaPlayer.start();
    }

    public void onMusicPause(View view){
        mediaPlayer.pause();

    }

    public void onMusicStop(View view){
        mediaPlayer.stop();
    }
}