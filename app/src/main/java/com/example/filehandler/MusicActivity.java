package com.example.filehandler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MusicActivity extends AppCompatActivity {

    public static final String LOG = MusicActivity.class.getSimpleName();

    public static final String SONG_NAME = "song_name";
    public static final String SONG_URI = "song_uri";
    public static final String SONG_POSITION = "song_position";

    Button play_pause, previous, next;

    AudioManager audioManager;
    MediaPlayer mediaPlayer;
    ViewPager2 viewPager;

    ArrayList<String> musicNames, musicUris;
    int position;

    TextView song_name, current_time, final_time;
    SeekBar mSeekBar;

    private final Handler handler = new Handler();
    private final Handler seekHandler = new Handler();

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        musicUris = new ArrayList<>();
        musicNames = new ArrayList<>();

        Intent intent = getIntent();
        if(intent.getStringArrayListExtra(SONG_NAME) != null & intent.getStringArrayListExtra(SONG_URI) != null){
            musicNames = intent.getStringArrayListExtra(SONG_NAME);
            musicUris = intent.getStringArrayListExtra(SONG_URI);
            position = intent.getIntExtra(SONG_POSITION, 0);
        } else {
            throw new IllegalArgumentException("data not found");
        }
        Log.e(LOG, "The size of names is " + musicNames.size());
        Log.e(LOG, "The size of uri is " + musicUris.size());

        play_pause = findViewById(R.id.play_pause_video);
        previous = findViewById(R.id.previous_video);
        next = findViewById(R.id.next_video);

        song_name = findViewById(R.id.name);
        current_time = findViewById(R.id.current_position);
        final_time = findViewById(R.id.final_position);

        mSeekBar = findViewById(R.id.seek_bar);

        viewPager = findViewById(R.id.view_pager);
        MusicImageAdapter adapter = new MusicImageAdapter(this, musicUris);

        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setAdapter(adapter);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(100));
        viewPager.setPageTransformer(transformer);
        viewPager.registerOnPageChangeCallback(mOnPageChangeListener);

        try {
            startMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }

        play_pause.setOnClickListener(v -> play_pause());

        previous.setOnClickListener(v -> {
            try {
                previousSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        next.setOnClickListener(v -> {
            try {
                nextSong();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    public void play_pause() {
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()) pause();
            else resume();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pause(){
        mediaPlayer.pause();
        handler.removeCallbacks(runnable);
        seekHandler.removeCallbacks(seekBarRunnable);
        play_pause.setBackground(getDrawable(R.drawable.ic_baseline_play_arrow_24));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    public void resume() {
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            runnable.run();
            seekBarRunnable.run();
            play_pause.setBackground(getDrawable(R.drawable.ic_baseline_pause_24));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startMusic() throws IOException {

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        assert audioManager != null;
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            Log.e(LOG, "The uri is " + Uri.parse(musicUris.get(position)));
            mediaPlayer.setDataSource(this, Uri.parse(musicUris.get(position)));
            mediaPlayer.prepare();
            mediaPlayer.start();

            play_pause.setBackground(getDrawable(R.drawable.ic_baseline_pause_24));
            final_time.setText(time(mediaPlayer.getDuration()));
            runnable.run();
            song_name.setText(musicNames.get(position));
            song_name.setSelected(true);

            mSeekBar.setMax(mediaPlayer.getDuration()/1000);
            seekBarRunnable.run();
            mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
            viewPager.setCurrentItem(position);

            mediaPlayer.setOnCompletionListener(mp -> {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void nextSong() throws IOException {
        if(position == musicUris.size() - 1) position = 0;
        else position++;
        changeAudio();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void previousSong() throws IOException {
        if(position == 0) position = musicUris.size() - 1;
        else position--;
        changeAudio();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void changeAudio() throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
        startMusic();
    }

    ViewPager2.OnPageChangeCallback mOnPageChangeListener = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int state) {
            if(state > position){
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (state < position){
                try {
                    previousSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.onPageSelected(position);
        }
    };

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = focusChange -> {
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK || focusChange == AUDIOFOCUS_LOSS){
            pause();
        }else if(focusChange == AUDIOFOCUS_GAIN){
            resume();
        }
    };

    private String time(int time_int){
        time_int = time_int/1000;
        String output = "";
        int sec = time_int % 60;
        int min = time_int / 60;

        output += min + ":";

        if(sec >= 10) output += sec + "";
        else output += "0" + sec;
        return output;
    }

    public Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            current_time.setText(time(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(runnable, 1000);
        }
    };

    public Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            mSeekBar.setProgress(mediaPlayer.getCurrentPosition()/1000);
            seekHandler.postDelayed(seekBarRunnable, 1000);
        }
    };

    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                seekBar.setProgress(progress);
                mediaPlayer.seekTo(progress*1000);
                current_time.setText(time(mediaPlayer.getCurrentPosition()));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.unregisterOnPageChangeCallback(mOnPageChangeListener);
    }

}
