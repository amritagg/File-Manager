package com.example.filehandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    ArrayList<String> image_uris;
    public static final String BUNDLE = "bundle";
    public static final String CURRENT_POSITION = "current_position";
    public static final String IMAGE_URIS = "image_uris";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(BUNDLE);

        assert bundle != null;
        int current = bundle.getInt(CURRENT_POSITION);

        image_uris = bundle.getStringArrayList(IMAGE_URIS);
        viewPager = findViewById(R.id.image_view);
        ImageAdapter adapter = new ImageAdapter(this, image_uris);

        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setAdapter(adapter);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(100));
        viewPager.setPageTransformer(transformer);
        viewPager.setCurrentItem(current);
    }
}