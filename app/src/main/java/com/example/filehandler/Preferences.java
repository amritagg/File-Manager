package com.example.filehandler;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class Preferences {

    public static final String SHOW_HIDE_KEY = "show_hide";
    private static final boolean isHide = true;

    public static final String LIST_GRID_KEY = "list_grid";
    private static final boolean isList = true;

    synchronized public static void setShowHide(Context context, boolean isHide){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_HIDE_KEY, isHide);
        editor.apply();
    }

    synchronized public static void setListGrid(Context context, boolean isList){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LIST_GRID_KEY, isList);
        editor.apply();
    }

    public static boolean getShowHide(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SHOW_HIDE_KEY, isHide);
    }

    public static boolean getListGrid(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(LIST_GRID_KEY, isList);
    }

}
