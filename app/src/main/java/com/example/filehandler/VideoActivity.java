package com.example.filehandler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    public static final String LOG = VideoActivity.class.getSimpleName();

    VideoView videoView;
    TextView current_time, final_time;
    Button previous, play_pause, next;
    FrameLayout layout;
    SeekBar mSeekBar;

    public static final String VIDEO_URI = "video_uri";
    public static final String VIDEO_NAME = "video_name";
    public static final String VIDEO_POSITION = "position";

    ArrayList<String> video_uris, video_names;
    int position_current, hide;

    ActionBar actionBar;
    View decorView;
    boolean show_icons;

    private final Handler handler = new Handler();
    private final Handler seekHandler = new Handler();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        actionBar = getSupportActionBar();

        decorView = getWindow().getDecorView();
        hide = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(hide);

        show_icons = true;

        video_uris = new ArrayList<>();
        video_names = new ArrayList<>();

        Intent intent = getIntent();
        video_uris = intent.getStringArrayListExtra(VIDEO_URI);
        video_names = intent.getStringArrayListExtra(VIDEO_NAME);
        position_current = intent.getIntExtra(VIDEO_POSITION, 0);

        init();
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        startVideo();

        play_pause.setOnClickListener(v -> {
            if(videoView.isPlaying()) pause();
            else resume();
        });
        previous.setOnClickListener(v -> play_previous());
        next.setOnClickListener(v -> play_next());
        videoView.setOnClickListener(v -> show_hide());
    }

    private void show_hide() {
        if(show_icons){
            layout.setVisibility(View.INVISIBLE);
            actionBar.hide();
            show_icons = false;
        }else {
            layout.setVisibility(View.VISIBLE);
            actionBar.show();
            show_icons = true;
        }
    }

    private void init(){
        videoView = findViewById(R.id.video_view);
        current_time = findViewById(R.id.current_time);
        final_time = findViewById(R.id.final_time);
        previous = findViewById(R.id.previous_video);
        next = findViewById(R.id.next_video);
        play_pause = findViewById(R.id.play_pause_video);
        layout = findViewById(R.id.frame);
        mSeekBar = findViewById(R.id.seek_bar);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void startVideo(){
        play_pause.setBackground(getDrawable(R.drawable.ic_baseline_pause_24));
        videoView.setVideoURI(Uri.parse(video_uris.get(position_current)));
        videoView.start();
        Log.e(LOG, "The length is " + videoView.getDuration());
        layout.setVisibility(View.VISIBLE);
        setTitle(video_names.get(position_current));
        show_icons = true;
        current_time.setText("0:00:00");
        runnable.run();
        seekBarRunnable.run();
        final_time.setText(time(videoView.getDuration()));
        videoView.setOnCompletionListener(mp -> play_next());
    }

    private void play_next(){
        if(position_current == video_uris.size()-1) position_current = 0;
        else position_current++;
        startVideo();
    }

    private void play_previous(){
        if(position_current == 0) position_current = video_names.size()-1;
        else position_current--;
        startVideo();
    }

    private String time(int time_int){
        time_int = time_int/1000;
        String output;

        int hour = time_int / 3600;
        time_int %= 60;
        int min = time_int / 60;
        int sec = time_int % 60;

        if(hour < 10) output = "0" + hour + ":";
        else output = hour + ":";

        if (min < 10) output = output + "0" + min + ":";
        else output = output + min + ":";

        if (sec < 10) output = output + "0" + sec;
        else output = output + sec;

        return output;
    }

    public Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            current_time.setText(time(videoView.getCurrentPosition()));
            handler.postDelayed(runnable, 1000);
        }
    };

    public Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(videoView.getCurrentPosition()/1000);
            seekHandler.postDelayed(seekBarRunnable, 1000);
        }
    };

    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                seekBar.setProgress(progress);
                videoView.seekTo(progress*1000);
                current_time.setText(time(videoView.getCurrentPosition()));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            pause();
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            resume();
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pause(){
        videoView.pause();
        play_pause.setBackground(getDrawable(R.drawable.ic_baseline_play_arrow_24));
        handler.removeCallbacks(runnable);
        seekHandler.removeCallbacks(seekBarRunnable);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void resume(){
        videoView.start();
        play_pause.setBackground(getDrawable(R.drawable.ic_baseline_pause_24));
        runnable.run();
        seekBarRunnable.run();
    }
}