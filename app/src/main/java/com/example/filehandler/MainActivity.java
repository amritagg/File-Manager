package com.example.filehandler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG = MainActivity.class.getSimpleName();

    List<File> folderNames;
    ListView mListView;
    ListViewAdapter listadapter;
    int FoldersLocation = 0;
    File file;
    File[] f;

    //        request code for permission
    private final int REQUEST_PERMISSION_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        calling permissions method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionStatus();
        }

    }

    private void show_files() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            file = Environment.getExternalStorageDirectory();
            f = file.listFiles();

            FoldersLocation++;
            folderNames = new ArrayList<>();

            if (f != null) Collections.addAll(folderNames, f);

            mListView = findViewById(R.id.list_view);
            listadapter = new ListViewAdapter(this, folderNames);
            mListView.setAdapter(listadapter);

            mListView.setOnItemClickListener(mOnItemClickListener);

        }
    }

    ListView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            file = folderNames.get(position);

            if(file.isDirectory()){
                folderNames.clear();
                File[] file_array = file.listFiles();
                if (f != null) {
                    assert file_array != null;
                    Collections.addAll(folderNames, file_array);
                }
                mListView.setAdapter(listadapter);
                FoldersLocation++;
            } else if(file.isFile()){
                if(ImageUtils.isImage(file.getName())){

                    ArrayList<String> uris = new ArrayList<>();

                    for(int i = 0; i < folderNames.size(); i++){
                        if(ImageUtils.isImage(folderNames.get(i).getName())){
                            uris.add(ImageUtils.getUri(folderNames.get(i)));
                        }
                    }

                    String uri = ImageUtils.getUri(file);
                    int current = 0;
                    for(int i = 0; i < uris.size(); i++){
                        if(uris.get(i).equals(uri)) {
                            current = i;
                            break;
                        }
                    }

                    Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ImageActivity.IMAGE_URIS, uris);
                    bundle.putInt(ImageActivity.CURRENT_POSITION, current);

                    intent.putExtra(ImageActivity.BUNDLE, bundle);
                    FoldersLocation++;
                    startActivity(intent);

                } else if(AudioUtils.isAudio(file.getName())){

                    ArrayList<String> uris = new ArrayList<>();
                    ArrayList<String> names = new ArrayList<>();

                    for(int i = 0; i < folderNames.size(); i++){
                        if(AudioUtils.isAudio(folderNames.get(i).getName())){
                            uris.add(AudioUtils.getUri(folderNames.get(i)));
                            names.add(AudioUtils.getName(folderNames.get(i)));
                        }
                    }

                    String uri = folderNames.get(position).getAbsolutePath();
                    int current = 0;
                    for(int i = 0; i < uris.size(); i++){
                        if(uris.get(i).equals(uri)) {
                            current = i;
                            break;
                        }
                    }

                    Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                    intent.putStringArrayListExtra(MusicActivity.SONG_URI, uris);
                    intent.putStringArrayListExtra(MusicActivity.SONG_NAME, names);
                    intent.putExtra(MusicActivity.SONG_POSITION, current);
                    FoldersLocation++;
                    startActivity(intent);

                } else if(VideoUtils.isVideo(file.getName())){

                    ArrayList<String> uris = new ArrayList<>();
                    ArrayList<String> names = new ArrayList<>();

                    for(int i = 0; i < folderNames.size(); i++){
                        if(VideoUtils.isVideo(folderNames.get(i).getName())){
                            uris.add(VideoUtils.getUri(folderNames.get(i)));
                            names.add(VideoUtils.getName(folderNames.get(i)));
                        }
                    }

                    String uri = VideoUtils.getUri(folderNames.get(position));
                    int current = 0;
                    for(int i = 0; i < uris.size(); i++){
                        if(uris.get(i).equals(uri)) {
                            current = i;
                            break;
                        }
                    }

                    Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                    intent.putStringArrayListExtra(VideoActivity.VIDEO_URI, uris);
                    intent.putStringArrayListExtra(VideoActivity.VIDEO_NAME, names);
                    intent.putExtra(VideoActivity.VIDEO_POSITION, current);
                    FoldersLocation++;
                    startActivity(intent);

                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(FoldersLocation > 1){
            file = file.getParentFile();
            assert file != null;
            File[] f = file.listFiles();
            folderNames.clear();
            if (f != null) Collections.addAll(folderNames, f);
            mListView.setAdapter(listadapter);
            FoldersLocation--;
        }else super.onBackPressed();
    }

    //    list of permissions required
    String[] PERMISSION_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //  Method for permission handling
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkPermissionStatus() {

//      check weather the permission is given or not
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            show_files();
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                if not permission is denied than show the message to user showing him the benefits of the permission.
                Toast.makeText(this, "Permission is needed to show the Media..", Toast.LENGTH_SHORT).show();
                finish();
            }
//            request permission
            requestPermissions(PERMISSION_LIST, REQUEST_PERMISSION_CODE);
        }
    }

    //    handling the permissions
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) show_files();
            else{
                Toast.makeText(this,"Permissions not granted!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}