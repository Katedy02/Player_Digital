package com.example.player_digital;

import static com.example.player_digital.MusicListActivity.REQUEST_PERMISSION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTime, tvDuration,tvTitle, tvArtist;
    SeekBar seekBarTime, seekBarVolume;
    ImageButton btnPlay;

    MediaPlayer Player_Digital;

    ViewFlipper viewFlipper;
    ImageView next;
    ImageView previous;

    ArrayList<Song> songArrayList;

    private Song song;

    private int mediaIter=0;
    private final String TAG = "MainActivity";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        songArrayList = new ArrayList<>();

        checkPermission();

        getSongs();

        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        next = findViewById(R.id.next);
        previous= findViewById(R.id.previous);
        Player_Digital = new MediaPlayer();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaIter < songArrayList.size()) {
                    Log.d("hgf", "cde");
                    mediaIter =mediaIter +1;
                    Player_Digital.reset();
                    playMedia();
                    setMeta();
                }
                Log.d("ggg","iteer" +mediaIter);
            }


        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaIter>=1){
                    mediaIter=mediaIter-1;
                    Player_Digital.reset();
                    playMedia();
                    setMeta();
                }
                Log.d("ggg","iteer" +mediaIter);
            }
        });

        song = songArrayList.get(mediaIter);
        tvTime=findViewById(R.id.tvTime);
        tvDuration=findViewById(R.id.tvDuration);
        seekBarTime=findViewById(R.id.seekBarTime);
        seekBarVolume=findViewById(R.id.seekBarVolume);
        btnPlay = findViewById(R.id.btnPlay);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);

        setMeta();


        try {
            Player_Digital.setDataSource(song.getPath());
            Player_Digital.prepare();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void playMedia() {
        try {
            Player_Digital.setDataSource(songArrayList.get(mediaIter).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        try {
            Player_Digital.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Player_Digital.start();
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
    
    private void getSongs() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String title = songCursor.getString(indexTitle);
                String artist = songCursor.getString(indexArtist);
                String path = songCursor.getString(indexData);
                songArrayList.add(new Song(title, artist, path));
                Log.d(TAG, "getSongs: "+songArrayList.size());
            } while (songCursor.moveToNext());
            Log.d(TAG, "getSongs: "+songArrayList.size());
        }

    }
    private void checkPermission(){
        String[] permissions = new String[] {
                Manifest.permission.READ_MEDIA_AUDIO

        };
        boolean areAllPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                areAllPermissionsGranted = false;
                break;
            }
        }

        if (areAllPermissionsGranted) {
            // Permissions are already granted
        } else {
            // Permissions are not granted, request the permissions
        }
        ActivityResultLauncher<String[]> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions2 -> {
                    boolean allPermissionsGranted = true;
                    for (String permission : permissions2.keySet()) {
                        if (!permissions2.get(permission)) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        // Permissions are granted
                    } else {
                        // Permissions are denied
                    }
                });

        requestPermissionLauncher.launch(permissions);
    }

    private void setMeta(){
        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
    }
}