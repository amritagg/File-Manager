package com.example.filehandler;

import android.net.Uri;

import java.io.File;

public class AudioUtils {
    public static boolean isAudio(String name){

        String[] ext = {".mp3", ".wav", ".amr", ".flac", ".aac", ".ogg"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

    public static String getUri(File file) {
        return Uri.fromFile(file).toString();
    }

    public static String getName(File file){
        return file.getName();
    }
}
