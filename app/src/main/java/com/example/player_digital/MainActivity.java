package com.example.player_digital;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;

import com.example.player_digital.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTime, tvDuration;
    SeekBar seekBarTime, seekBarVolume;
    ImageButton btnPlay;

    MediaPlayer Player_Digital;
    Thread updateThread;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvTime=findViewById(R.id.tvTime);
        tvDuration=findViewById(R.id.tvDuration);
        seekBarTime=findViewById(R.id.seekBarTime);
        seekBarVolume=findViewById(R.id.seekBarVolume);
        btnPlay = findViewById(R.id.btnPlay);


        Player_Digital = MediaPlayer.create(this, R.raw.bella_ciao);
        Player_Digital.setLooping(true);
        Player_Digital.seekTo(0);
        Player_Digital.setVolume(0.2f,0.2f);


        String duration = millisecondsToString(Player_Digital.getDuration());
        tvDuration.setText(duration);

        btnPlay.setOnClickListener(this);

        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                float volume = progress / 100f;
                Player_Digital.setVolume(volume,volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarTime.setMax(Player_Digital.getDuration());
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if(isFromUser) {
                    Player_Digital.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Player_Digital != null && Player_Digital.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                        private void updateUI() {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (Player_Digital != null) {
                    if(Player_Digital.isPlaying()) {
                        try {
                            final double current = Player_Digital.getCurrentPosition();
                            final String elapsedTime = millisecondsToString((int) current);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTime.setText(elapsedTime);
                                    seekBarTime.setProgress((int) current);
                                }
                            });
                            Thread.sleep(1000);
                        }catch (InterruptedException e) {}
                    }
                }
            }
        }).start();
    }
    public String millisecondsToString(int time) {
        String elapsedTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes+":";
        if(seconds < 10) {
            elapsedTime += "0";
        }
        elapsedTime += seconds;
        return  elapsedTime;
    }



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnPlay) {
            if(Player_Digital.isPlaying()) {
                // is playing
                Player_Digital.pause();
                btnPlay.setBackgroundResource(R.drawable.ic_play);
            } else {
                // on pause
                Player_Digital.start();
                btnPlay.setBackgroundResource(R.drawable.pause);
            }
        }
    }
}









