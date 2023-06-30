package com.example.player_digital;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvTime, tvDuration;
    SeekBar seekBarTime, seekBarVolume;
    ImageButton play;

    MediaPlayer Player_Digital;
    Thread updateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvTime=findViewById(R.id.tvTime);
        tvDuration=findViewById(R.id.tvDuration);
        seekBarTime=findViewById(R.id.seekBarTime);
        seekBarVolume=findViewById(R.id.seekBarVolume);
        play = findViewById(R.id.play);

        Player_Digital = MediaPlayer.create(this, R.raw.bad_bunny_mayores);
        Player_Digital.setLooping(true);
        Player_Digital.seekTo(0);
        Player_Digital.setVolume(0.2f,0.2f);


        Player_Digital.start();
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Player_Digital != null && Player_Digital.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateThread.start();
    }

    private void updateUI() {
        int current = Player_Digital.getCurrentPosition();
        int duration = Player_Digital.getDuration();

        tvTime.setText(millisecondsToString(current));
        tvDuration.setText(millisecondsToString(duration));
        seekBarTime.setMax(duration);
        seekBarTime.setProgress(current);
    }

    private String millisecondsToString(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}







