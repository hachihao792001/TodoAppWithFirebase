package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class RelaxingTaskDetail extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relaxing_task_detail);
    }

    public void onMusicStart(View view){
       mediaPlayer =MediaPlayer.create(this,R.raw.weigtless);
       mediaPlayer.start();
    }

    public void onMusicPause(View view){
        mediaPlayer.pause();

    }

    public void onMusicStop(View view){
        mediaPlayer.stop();
    }
}