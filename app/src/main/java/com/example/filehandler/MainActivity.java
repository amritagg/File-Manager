package com.example.filehandler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

//    public static final String LOG = MainActivity.class.getSimpleName();

    public boolean isHidden = true;
    public boolean isList = true;

    List<File> folderNames;
    ListView mListView;
    GridView mGridView;
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

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void show_files() {

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            file = Environment.getExternalStorageDirectory();
            f = file.listFiles();

            FoldersLocation++;
            folderNames = new ArrayList<>();

            isHidden = Preferences.getShowHide(this);
            if (f != null){
                for (File value : f) {
                    if (isHidden) {
                        if (!value.isHidden()) {
                            folderNames.add(value);
                        }
                    } else {
                        folderNames.add(value);
                    }
                }
            }
//                Collections.addAll(folderNames, f);

            mListView = findViewById(R.id.list_view);
            mGridView = findViewById(R.id.grid_view);
            listadapter = new ListViewAdapter(this, folderNames, Preferences.getListGrid(this));

            if(Preferences.getListGrid(this)){
                mListView.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.GONE);
                mListView.setAdapter(listadapter);
                mListView.setOnItemClickListener(mOnItemClickListener);
            }else {
                mListView.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mGridView.setAdapter(listadapter);
                mGridView.setOnItemClickListener(mOnItemClickListener);
            }
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
                    for (File value : file_array) {
                        if (isHidden) {
                            if (!value.isHidden()) {
                                folderNames.add(value);
                            }
                        } else {
                            folderNames.add(value);
                        }
                    }
//                    Collections.addAll(folderNames, file_array);
                }
                if(Preferences.getListGrid(getApplicationContext())){
                    mListView.setAdapter(listadapter);
                }else {
                    mGridView.setAdapter(listadapter);
                }
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
            if (f != null) {
                for (File value : f) {
                    if (isHidden) {
                        if (!value.isHidden()) {
                            folderNames.add(value);
                        }
                    } else {
                        folderNames.add(value);
                    }
                }
//                Collections.addAll(folderNames, f);
            }
            isList = Preferences.getListGrid(getApplicationContext());
            if(isList) mListView.setAdapter(listadapter);
            else mGridView.setAdapter(listadapter);
            FoldersLocation--;
        }else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        isList = Preferences.getListGrid(this);
        isHidden = Preferences.getShowHide(this);

        if(isHidden) menu.findItem(R.id.hide_show).setTitle(R.string.show_hidden_files);
        else menu.findItem(R.id.hide_show).setTitle(R.string.dont_show_hidden_files);

        if(isList) menu.findItem(R.id.list_grid).setTitle(R.string.grid_view);
        else menu.findItem(R.id.list_grid).setTitle(R.string.list_view);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.new_folder){
            show_dialog();
            return true;
        }else if(item.getItemId() == R.id.hide_show){
            show_hide(item);
            return true;
        }else if(item.getItemId() == R.id.list_grid){
            list_grid(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void show_dialog() {
        EditText editText = new EditText(this);
        editText.setSelected(true);
        editText.setPadding(16, 16, 16, 16);

        new AlertDialog.Builder(this)
                .setTitle("Enter the File name")
                .setView(editText)
                .setPositiveButton("Yes", (dialog, which) -> {
                    String name = editText.getText().toString();
                    File new_file = new File(file.getAbsolutePath() + "/" + name);
                    if(new_file.exists()){
                        Toast.makeText(getApplicationContext(), "Folder name already in use", Toast.LENGTH_SHORT).show();
                    }else {
                        boolean result = new_file.mkdir();
                        if(result) {
                            Toast.makeText(getApplicationContext(), "Folder created successfully", Toast.LENGTH_SHORT).show();
                            f = file.listFiles();
                            folderNames.clear();
                            if (f != null) Collections.addAll(folderNames, f);
                            mListView.setAdapter(listadapter);
                        }
                        else Toast.makeText(getApplicationContext(), "Some Error occurred", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", (dialog, which) -> Toast.makeText(getApplicationContext(), "The folder is not created", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void list_grid(MenuItem item){
        isList = Preferences.getListGrid(this);
        if(isList){
            mListView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            listadapter = new ListViewAdapter(this, folderNames, false);
            mGridView.setAdapter(listadapter);
            mGridView.setOnItemClickListener(mOnItemClickListener);
            Preferences.setListGrid(this, false);
            item.setTitle(getResources().getString(R.string.list_view));
        }else {
            mListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            listadapter = new ListViewAdapter(this, folderNames, true);
            mListView.setAdapter(listadapter);
            mListView.setOnItemClickListener(mOnItemClickListener);
            Preferences.setListGrid(this, true);
            item.setTitle(getResources().getString(R.string.grid_view));
        }
    }

    private void show_hide(MenuItem item){
        isHidden = Preferences.getShowHide(this);
        isList = Preferences.getListGrid(this);
        File[] file_array = file.listFiles();
        if (f != null){
            folderNames.clear();
            for (File value : file_array) {
                if (!isHidden) {
                    if (!value.isHidden()) {
                        folderNames.add(value);
                    }
                } else {
                    folderNames.add(value);
                }
            }
        }
        if(isList) {
            listadapter = new ListViewAdapter(this, folderNames, true);
            mListView.setAdapter(listadapter);
        }else {
            listadapter = new ListViewAdapter(this, folderNames, false);
            mGridView.setAdapter(listadapter);
        }
        if(isHidden){
            Preferences.setShowHide(this, false);
            item.setTitle(getResources().getString(R.string.dont_show_hidden_files));
        }else {
            Preferences.setShowHide(this, true);
            item.setTitle(getResources().getString(R.string.show_hidden_files));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(Preferences.SHOW_HIDE_KEY.equals(key)){
            isHidden = Preferences.getShowHide(this);
        }
        if(Preferences.LIST_GRID_KEY.equals(key)){
             isList = Preferences.getListGrid(this);
        }
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