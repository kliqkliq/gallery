package eu.kliq.gallery.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;

import eu.kliq.gallery.adapter.ImageAdapter;
import eu.kliq.gallery.R;

public class ImageActivity extends AppCompatActivity {

    private ImageAdapter mImageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image);

        final Bundle bundle = getIntent().getExtras();
        final int position = bundle.getInt("position");
        final ArrayList<String> images = bundle.getStringArrayList("images");

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mImageAdapter = new ImageAdapter(this, images);
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.setCurrentItem(position);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_background);
        frameLayout.setBackground(new ColorDrawable(Color.BLACK));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
