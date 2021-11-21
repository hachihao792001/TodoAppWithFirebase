package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class RelaxingTaskDetail extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    RelaxingTask thisTask;
    ArrayList<String> listSong = new ArrayList<>();
    String plName="Someone Like You";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relaxing_task_detail);
        initSongList();
        Intent intent = getIntent();
        thisTask = (RelaxingTask) intent.getSerializableExtra("task");
        TextView taskTextView = findViewById(R.id.task);
        TextView descTextView = findViewById(R.id.description);
        TextView dateTextView = findViewById(R.id.date);
        TextView playlistName= findViewById(R.id.playlistName);
        taskTextView.setText(thisTask.getTask());
        descTextView.setText(thisTask.getDescription());
        dateTextView.setText(thisTask.getDate());
        playlistName.setText(thisTask.getPlaylistName());
        plName=thisTask.getPlaylistName();

    }

    private void initSongList() {
        listSong.add("Weightless - Marconi Union");
        listSong.add("Electra - Airstream");
        listSong.add("Mellomaniac (Chill Out Mix) - DJ Shah");
        listSong.add("Watermark - Enya");
        listSong.add("Strawberry Swing - Coldplay");
        listSong.add("Please Don't Go - Barcelona");
        listSong.add("Pure Shores - All Saints");
        listSong.add("Someone Like You - Adele");
        listSong.add("Canzonetta Sull'aria - Mozart");
        listSong.add("We Can Fly - Rue du Soleil");
    }

    public void onMusicStart(View view){
       mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
       switch(plName){
           case "Weightless - Marconi Union":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Electra - Airstream":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Mellomaniac (Chill Out Mix) - DJ Shah":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Watermark - Enya":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Strawberry Swing - Coldplay":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Please Don't Go - Barcelona":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Pure Shores - All Saints":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Someone Like You - Adele":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "Canzonetta Sull'aria - Mozart":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           case "We Can Fly - Rue du Soleil":
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
           default:
               mediaPlayer = MediaPlayer.create(this,R.raw.weigtless);
               break;
       }

       mediaPlayer.start();
    }

    public void onMusicPause(View view){
        mediaPlayer.pause();

    }

    public void onMusicStop(View view){
        mediaPlayer.stop();
    }
}