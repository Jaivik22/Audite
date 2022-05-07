package com.example.audite;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class albumSongs extends AppCompatActivity {
    ListView listView;
    String albumName;
    String newAlbumName;
    TextView textView5;
    String[] items;
    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        listView = findViewById(R.id.listView);
        textView5  =findViewById(R.id.textView5);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        albumName = intent.getStringExtra("albumName");
        textView5.setText(albumName);
        newAlbumName = "/"+albumName+"/";

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        Toast.makeText(albumSongs.this, "Runtime Permission given", Toast.LENGTH_SHORT).show();
                        mySongs = fetchSongs(Environment.getExternalStorageDirectory());

                        items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size(); i++) {
                            items[i] = mySongs.get(i).getName().replace(".mp3", "");
                        }
                       AdapterMainActivity adapter = new AdapterMainActivity(albumSongs.this,R.layout.song_view,items,mySongs);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(albumSongs.this,PlaySongs.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList", mySongs);
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
    }
    public ArrayList<File> fetchSongs(File file) {
        ArrayList arrayList = new ArrayList();
        String path = Environment.getExternalStorageDirectory().toString()+newAlbumName;
        File directory = new File(path);
        File[] songs =directory.listFiles();
        for (File myFile: songs){
            if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                arrayList.add(myFile);
            }
        }
        return arrayList;
    }
}