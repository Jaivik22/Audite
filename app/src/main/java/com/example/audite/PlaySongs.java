package com.example.audite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PlaySongs extends AppCompatActivity {

    private static final String LIST_KEY = "list key";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView,startPoint,songLength,artistName;
    ImageView previous,next,play,favourite,musicArt;
    private static ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    double curr_duration,total_duration;
    static ArrayList<File> favouriteSongList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_songs);
        textView = findViewById(R.id.textView);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.seekBar);
        favourite = findViewById(R.id.favourite);
        musicArt = findViewById(R.id.musicArt);
        startPoint = findViewById(R.id.startPoint);
        songLength = findViewById(R.id.songLength);
        artistName = findViewById(R.id.artistName);
        loadData();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs  = (ArrayList) bundle.getParcelableArrayList("songList");
        int[] arr = new int[songs.size()];
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        favouriteSetup();


        Uri uri = Uri.parse(songs.get(position).toString());
        Uri uri01 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(mediaPlayer.isPlaying()){
                     play.setImageResource(R.drawable.play_white);
                     mediaPlayer.pause();
                 }
                 else{
                     play.setImageResource(R.drawable.pause_white);
                     mediaPlayer.start();
                 }
                if(arr[position]==0) {
                    favourite.setImageResource(R.drawable.favourite);
                }
                if(arr[position]==1) {
                    favourite.setImageResource(R.drawable.fillfavourite);
                }


            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position = position-1;
                }
                else{
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause_white);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                metaData(uri);
                setAudioProgress();
                favouriteSetup();



            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position+1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause_white);
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setMax((int)total_duration);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                metaData(uri);
                setAudioProgress();
                favouriteSetup();


            }
        });

//        for like button
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(favouriteSongList).contains(songs.get(position).getName())) {
                    favouriteSongList.remove(songs.get(position));
                    favourite.setImageResource(R.drawable.favourite);
                }
                else{
                    favouriteSongList.add(songs.get(position));
                    favourite.setImageResource(R.drawable.fillfavourite);

                }
                saveData();
            }

        });
        metaData(uri);
        setAudioProgress();


    }


    //set song art and artist name
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever =  new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        artistName.setText(artist);
        byte[] art = retriever.getEmbeddedPicture();
        if(art != null)
        {
            Glide.with(this)
                    .asBitmap().
                    load(art).
                    into(musicArt);
        }
        else{
            Glide.with(PlaySongs.this)
                    .asBitmap().
                    load(R.drawable.music02).
                    into(musicArt);
        }
    }

    //set audio duration and progress
    public void setAudioProgress(){
        //get audio duration
        curr_duration = mediaPlayer.getCurrentPosition();
        total_duration =mediaPlayer.getDuration();

        //display audio duration
        startPoint.setText(timeConversion((long)curr_duration));
        songLength.setText(timeConversion((long)total_duration));
        seekBar.setMax((int)total_duration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    curr_duration = mediaPlayer.getCurrentPosition();
                    startPoint.setText(timeConversion((long)curr_duration));
                    seekBar.setProgress((int)curr_duration);
                    handler.postDelayed(this,1000);
                }
                catch (IllegalStateException ed){
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable,1000);


    }

    private String timeConversion(double value){
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000/1000;

        if(hrs>0){
            audioTime = String.format("%02d:%02d:02d",hrs,mns,scs);
        }
        else{
            audioTime = String.format("%02d:%02d",mns,scs);
        }
        return audioTime;
    }
    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(favouriteSongList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("LIST_KEY", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }
public void loadData() {
    // method to load arraylist from shared prefs
    // initializing our shared prefs with name as
    // shared preferences.
    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

    // creating a variable for gson.
    Gson gson = new Gson();

    // below line is to get to string present from our
    // shared prefs if not present setting it as null.
    String json = sharedPreferences.getString("LIST_KEY", null);

    // below line is to get the type of our array list.
    Type type = new TypeToken<ArrayList<File>>() {}.getType();

    // in below line we are getting data from gson
    // and saving it to our array list
     favouriteSongList = gson.fromJson(json, type);

    // checking below if the array list is empty or not
    if (favouriteSongList== null) {
        // if the array list is empty
        // creating a new array list.
         favouriteSongList = new ArrayList<>();
    }
}
public void favouriteSetup(){
//        String s = songs.get(position).getName();
        if(String.valueOf(favouriteSongList).contains(songs.get(position).getName())){
            favourite.setImageResource(R.drawable.fillfavourite);
        }
        else{
            favourite.setImageResource(R.drawable.favourite);
        }
    }





}