package com.example.audite;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class album extends AppCompatActivity {
    ImageView user,home;
    ListView listView;
    boolean IsSongDir=false;
    ArrayList<File> filesName;
    String[] albumItems;

    LinearLayout favouritelist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        listView = findViewById(R.id.listview);
        home = findViewById(R.id.home);
        user = findViewById(R.id.user);
       favouritelist = findViewById(R.id.favouritelist);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

//                        ArrayList<File> songfiles = (ArrayList)getListFiles(Environment.getExternalStorageDirectory());
//                        String[] fileItems = new String[songfiles.size()];
//                        for(int i = 0;i<songfiles.size();i++) {
//                            fileItems[i] = songfiles.get(i).getName();
//                        }
//                        Adapter adapter = new Adapter(album.this, android.R.layout.simple_list_item_1,fileItems);
//                        listView.setAdapter(adapter);

                        filesName = (ArrayList) ListDir(Environment.getExternalStorageDirectory());
                        albumItems = new String[filesName.size()];
                        for(int i=0;i<filesName.size();i++){
                            albumItems[i] = filesName.get(i).getName();
                        }
                        AdapterAlbum adapter = new AdapterAlbum(album.this,R.layout.album_view,albumItems);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(album.this,albumSongs.class);
                                String albumName = listView.getItemAtPosition(position).toString();
                                intent.putExtra("albumItems",albumItems);
                                intent.putExtra("albumName",albumName);
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
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(album.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(album.this, MainActivity.class);
                startActivity(intent);
            }
        });
        favouritelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(album.this,favouriteList.class);
//                intent.putExtra("favouriteSongList",favouriteSongListList);
                startActivity(intent);
            }
        });
    }
//
    public List<String> ListDir(File f){
        List fileList = new ArrayList();
        File[] files = f.listFiles();
        for(File file : files){
                if (!file.isHidden() && file.isDirectory()) {
                    CheckSongDir(file);
                    if(IsSongDir) {
                        fileList.add(file);
                        IsSongDir = false;
                    }
                }
        }
        return fileList;
    }
    public Boolean CheckSongDir(File f){
        List fileList = new ArrayList();
        File[] files = f.listFiles();
        for(File file : files){
            if (file.getName().endsWith(".mp3") && !file.getName().startsWith(".")) {
                IsSongDir = true;
            }
        }
        return IsSongDir;
    }


}