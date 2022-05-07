package com.example.audite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.audite.PlaySongs.favouriteSongList;
public class favouriteList extends AppCompatActivity {

    String[] items;
    ListView listView;

//    private ArrayList courseModalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);
        listView = findViewById(R.id.listView);
        loadData();
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        favouriteSongs = (ArrayList) bundle.getParcelableArrayList("favouriteSongList");
        items = new String[favouriteSongList.size()];
        for (int i = 0; i < favouriteSongList.size(); i++) {
            items[i] = favouriteSongList.get(i).getName().replace(".mp3", "");
        }
        AdapterMainActivity adapter = new AdapterMainActivity(favouriteList.this,R.layout.song_view,items,favouriteSongList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(favouriteList.this,PlaySongs.class);
                String currentSong = listView.getItemAtPosition(position).toString();
                intent.putExtra("songList",favouriteSongList);
                intent.putExtra("currentSong", currentSong);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
//        saveData();

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
}