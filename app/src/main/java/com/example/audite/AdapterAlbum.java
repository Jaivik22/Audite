package com.example.audite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterAlbum extends ArrayAdapter<String> {

    private Context mContect;
    int mResource;



    public AdapterAlbum(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        this.mContect = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(mContect);
        convertView = inflater.inflate(mResource,parent,false);
        TextView folderName =  convertView.findViewById(R.id.folderName);
        ImageView folderImage = convertView.findViewById(R.id.folderImage);
        folderName.setText(name);
        return convertView;
    }
}
