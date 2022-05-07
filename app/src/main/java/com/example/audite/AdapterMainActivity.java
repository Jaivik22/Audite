package com.example.audite;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AdapterMainActivity extends ArrayAdapter<String> {
    ArrayList<File> mFiles;
    private Context mContext;
    int mResource;
    ImageView album_art;


    public AdapterMainActivity(@NonNull Context context, int resource, @NonNull String[] objects, ArrayList<File> mFiles) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Uri uri = Uri.parse(mFiles.get(position).toString());
        String name = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        TextView tv =  convertView.findViewById(R.id.tv);
        ImageView album_art = convertView.findViewById(R.id.songImage);
        tv.setText(name);
        byte[] image = metaData(mFiles.get(position).getPath());
        if(image !=null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(album_art);
        }
        else{
            Glide.with(mContext)
                    .load(R.drawable.music)
                    .into(album_art);
        }
        return convertView;
    }
    private byte[] metaData(String uri){
        MediaMetadataRetriever retriever =  new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }


}
