package com.example.player_digital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import android.widget.ViewFlipper;
import com.example.player_digital.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTime, tvDuration,tvTitle, tvArtist;
    SeekBar seekBarTime, seekBarVolume;
    ImageButton btnPlay;

    MediaPlayer Player_Digital;

    ViewFlipper viewFlipper;
    ImageView next;
    ImageView previous;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Song song =(Song) getIntent().getSerializableExtra("song");

        viewFlipper=(ViewFlipper)findViewById(R.id.viewFlipper);
        next = findViewById(R.id.next);
        previous= findViewById(R.id.previous);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("hgf","cde");
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ggg","dqw");
            }
        });


        tvTime=findViewById(R.id.tvTime);
        tvDuration=findViewById(R.id.tvDuration);
        seekBarTime=findViewById(R.id.seekBarTime);
        seekBarVolume=findViewById(R.id.seekBarVolume);
        btnPlay = findViewById(R.id.btnPlay);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        Player_Digital = new  MediaPlayer();
        try {
            Player_Digital.setDataSource(song.getPath());
            Player_Digital.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Player_Digital.setLooping(true);
        Player_Digital.seekTo(0);
        Player_Digital.setVolume(0.5f,0.5f);


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
        if (view == next) {
            viewFlipper.showNext();
        }
        else if (view == previous) {
            viewFlipper.showPrevious();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId()==android.R.id.home){
            finish();
            if(Player_Digital.isPlaying()){
                Player_Digital.stop();
            }
        }
            return super.onOptionsItemSelected(item);
    }

}











