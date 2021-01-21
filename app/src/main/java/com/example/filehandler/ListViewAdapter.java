package com.example.filehandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {


    private final Context mContext;
    private final List<File> mFolderName;

    public ListViewAdapter(Context mContext, List<File> mFolderName) {
        this.mContext = mContext;
        this.mFolderName = mFolderName;
    }

    @Override
    public int getCount() {
        return mFolderName.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.list_item,null);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.folder);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = mFolderName.get(position);
        String name_string = file.getName();
        name.setText(name_string);

        if(!mFolderName.get(position).isDirectory()){
            if(ImageUtils.isImage(name_string)){
                image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_image_24));
                Glide.with(mContext).load(file).into(image);
            }else if(AudioUtils.isAudio(name_string)){
                image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_audiotrack_24));
            }else if(VideoUtils.isVideo(name_string)){
                image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_videocam_24));
            }
        }else {
            image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_folder_24));
        }

        return convertView;
    }

}
