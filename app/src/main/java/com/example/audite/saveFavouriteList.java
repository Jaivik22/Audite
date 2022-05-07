package com.example.audite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class saveFavouriteList {
    private static final String LIST_KEY = "list_key" ;

    public static void writeSongList(Context context, ArrayList list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LIST_KEY,json);
        editor.apply();

    }
    public static ArrayList readListFromPref(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(LIST_KEY,"");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<PlaySongs>>(){}.getType();
        ArrayList list = gson.fromJson(jsonString,type);
        return list;

    }
}
