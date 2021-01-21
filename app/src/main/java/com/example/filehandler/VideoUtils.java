package com.example.filehandler;

import android.net.Uri;
import java.io.File;

public class VideoUtils {

    public static boolean isVideo(String name){

        String[] ext = {".3gp", ".mp4", ".mkv", ".webm"};
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
