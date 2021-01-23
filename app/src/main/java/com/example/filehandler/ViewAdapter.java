package com.example.filehandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
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

public class ViewAdapter extends BaseAdapter {

    private static final String LOG = ViewAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<File> mFolderName;
    private final boolean isList;

    ImageView image;
    TextView name;
    String name_string;

    public ViewAdapter(Context mContext, List<File> mFolderName, boolean isList) {
        this.mContext = mContext;
        this.mFolderName = mFolderName;
        this.isList = isList;
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
    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables", "StaticFieldLeak"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            if(isList) convertView = layoutInflater.inflate(R.layout.list_item,null);
            else convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }

        name = convertView.findViewById(R.id.name);
        image = convertView.findViewById(R.id.folder);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = mFolderName.get(position).getAbsoluteFile();
        name_string = file.getName();
        name.setText(name_string);

        if(!mFolderName.get(position).isDirectory()){
            if(ImageUtils.isImage(name_string)){
                Glide.with(mContext)
                        .load(file)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .into(image);
            }else if(MusicUtils.isAudio(name_string)){

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getAbsolutePath());

                    Glide.with(mContext)
                            .load(retriever.getEmbeddedPicture())
                            .error(R.drawable.ic_baseline_audiotrack_24)
                            .placeholder(R.drawable.ic_baseline_audiotrack_24)
                            .into(image);

                retriever.release();
            }else if(VideoUtils.isVideo(name_string)){

                Glide.with(mContext)
                        .load(file)
                        .placeholder(R.drawable.ic_baseline_videocam_24)
                        .error(R.drawable.ic_baseline_videocam_24)
                        .into(image);

            }
        }else image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_folder_24));

        return convertView;
    }

}
