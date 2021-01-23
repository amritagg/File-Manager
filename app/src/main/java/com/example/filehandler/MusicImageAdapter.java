package com.example.filehandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicImageAdapter extends RecyclerView.Adapter<MusicImageAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<String> imageUris;

    public MusicImageAdapter(Context mContext, ArrayList<String> imageUris) {
        this.mContext = mContext;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public MusicImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_card, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MusicImageAdapter.ViewHolder holder, int position) {

        File file = new File(imageUris.get(position));
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createAudioThumbnail(file,
                    new Size(640, 640),
                    null);
        } catch (IOException e) {
            Log.e("MusicImageAdapter", "The error is " + e);
        }

        if(bitmap != null) holder.imageView.setImageBitmap(bitmap);
        else holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.music));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
