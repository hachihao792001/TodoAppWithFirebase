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
    String song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relaxing_task_detail);
        Intent intent = getIntent();
        thisTask = (RelaxingTask) intent.getSerializableExtra("task");
        TextView taskTextView = findViewById(R.id.task);
        TextView descTextView = findViewById(R.id.description);
        TextView dateTextView = findViewById(R.id.date);
        TextView songName = findViewById(R.id.songName);
        taskTextView.setText(thisTask.getTask());
        descTextView.setText(thisTask.getDescription());
        dateTextView.setText(thisTask.getDate());
        songName.setText(thisTask.getPlaylistName());
        song = songName.toString();
    }


    public void onMusicStart(View view) {
        if (mediaPlayer != null) return;
        switch (song) {
            case "Weightless - Marconi Union":
                mediaPlayer = MediaPlayer.create(this, R.raw.weigtless);
                break;
            case "Electra - Airstream":
                mediaPlayer = MediaPlayer.create(this, R.raw.electra);
                break;
            case "Mellomaniac (Chill Out Mix) - DJ Shah":
                mediaPlayer = MediaPlayer.create(this, R.raw.mellomaniac);
                break;
            case "Watermark - Enya":
                mediaPlayer = MediaPlayer.create(this, R.raw.watermark);
                break;
            case "Strawberry Swing - Coldplay":
                mediaPlayer = MediaPlayer.create(this, R.raw.strawberry_swing);
                break;
            case "Please Dont Go - Barcelona":
                mediaPlayer = MediaPlayer.create(this, R.raw.please_don_t_go);
                break;
            case "Pure Shores - All Saints":
                mediaPlayer = MediaPlayer.create(this, R.raw.pure_shores);
                break;
            case "Someone Like You - Adele":
                mediaPlayer = MediaPlayer.create(this, R.raw.someone_like_you);
                break;
            case "We Can Fly - Rue du Soleila":
                mediaPlayer = MediaPlayer.create(this, R.raw.we_can_fly);
                break;
            default:
                mediaPlayer = MediaPlayer.create(this, R.raw.someone_like_you);
                break;
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.someone_like_you);
        mediaPlayer.start();
    }

    public void onMusicPause(View view) {
        mediaPlayer.pause();

    }

    public void onMusicStop(View view) {
        mediaPlayer.stop();
    }
}