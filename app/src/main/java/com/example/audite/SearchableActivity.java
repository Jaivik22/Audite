package com.example.audite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView ;
import android.widget.ArrayAdapter;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class SearchableActivity extends AppCompatActivity {
    SearchView mysearchView;
    ListView mylist;
    ArrayList<File> songs;
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        mysearchView = (SearchView) findViewById(R.id.mysearchView);
        mylist = (ListView)findViewById(R.id.listSearchView);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs  =(ArrayList) bundle.getParcelableArrayList("songList");
        String[] items = new String[songs.size()];
        for(int i = 0;i<songs.size();i++){
            items[i] =  songs.get(i).getName().replace(".mp3","");
        }



      adapter = new ArrayAdapter<>(SearchableActivity.this, android.R.layout.simple_list_item_1,items);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchableActivity.this,PlaySongs.class);
                String currentSong = mylist.getItemAtPosition(position).toString();
                intent.putExtra("songList",songs);
                intent.putExtra("currentSong",currentSong);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        mysearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return false;
            }
        });
    }


}