package com.example.filehandler;

import android.net.Uri;
import java.io.File;

public class ImageUtils {
    public static boolean isImage(String name){
        String[] ext = {".jpg", ".png", ".gif", ".bmp", ".webp", ".heic", ".heif"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

    public static String getUri(File file) {
        return Uri.fromFile(file).toString();
    }
}
