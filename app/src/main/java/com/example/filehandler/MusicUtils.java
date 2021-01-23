package com.example.filehandler;

import android.net.Uri;

import java.io.File;

public class MusicUtils {
    public static boolean isAudio(String name){

        String[] ext = {".mp3", ".wav", ".amr", ".flac", ".aac", ".ogg"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

    public static String getName(File file){
        return file.getName();
    }
}
