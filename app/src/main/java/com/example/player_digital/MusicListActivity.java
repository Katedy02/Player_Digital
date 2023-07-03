package com.example.player_digital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {
ArrayList<Song> songArrayList;
ListView lvSongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        lvSongs = findViewById(R.id.lvSongs);

        songArrayList= new ArrayList<>();
        for(int i=0;i<=100;i++)
            songArrayList.add(new Song("Song "+i,"Artist "+i,"Path "+i));
        adapter adapter = new adapter(this,songArrayList);
        lvSongs.setAdapter(adapter);



    }
}