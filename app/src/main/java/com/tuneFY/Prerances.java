package com.tuneFY;

import android.content.Context;
import android.content.SharedPreferences;

public class Prerances {

    public static SharedPreferences getPref(Context c){
        SharedPreferences sharedpreferences = c.getSharedPreferences("TunefyPrefs", Context.MODE_PRIVATE);
        return sharedpreferences;
    }

    public static void  save_audio_source(Context c,int source){
        SharedPreferences pref = getPref(c);
        pref.edit().putInt("tunify_audio_source",source).apply();

    }

    public static int  save_audio_source(Context c){
        SharedPreferences pref = getPref(c);
       return pref.getInt("tunify_audio_source",0);

    }

    public static void  volume_music(Context c,int vol){
        SharedPreferences pref = getPref(c);
        pref.edit().putInt("tunify_audio_volume",vol).apply();

    }

    public static int  volume_music(Context c){
        SharedPreferences pref = getPref(c);
        return pref.getInt("tunify_audio_volume",10);

    }




}
