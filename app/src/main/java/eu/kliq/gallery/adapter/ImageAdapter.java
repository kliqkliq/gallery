package eu.kliq.gallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import eu.kliq.gallery.json.JsonItem;
import eu.kliq.gallery.glide.ProgressTarget;
import eu.kliq.gallery.R;
import eu.kliq.gallery.activity.MainActivity;

public class ImageAdapter extends PagerAdapter {

    public static final String LOG_TAG = "ImageAdapter";

    private Context mContext;
    private ArrayList<String> mImages;
    private boolean mShowProgressBar;

    public ImageAdapter(Context context, ArrayList<String> images) {
        mContext = context;
        mImages = images;

        final Activity activity = (Activity) mContext;
        final SharedPreferences sharedPref = activity.getSharedPreferences(MainActivity.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        mShowProgressBar = sharedPref.getBoolean(MainActivity.PREF_SHOW_PROGRESS_BAR_KEY, MainActivity.DEFAULT_PROGRESS_BAR_STATUS);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {

        RelativeLayout layout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        final ImageView imageView = new PhotoView(mContext);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        layout.addView(imageView);

        String baseUrl = mImages.get(i);
        String thumbURL = JsonItem.getThumbUrl(baseUrl);
        String imageURL = JsonItem.getImageUrl(baseUrl);

        BitmapTypeRequest<String> thumbnailRequest = Glide.with(mContext).load(thumbURL).asBitmap();

        final NumberProgressBar progressBar = new NumberProgressBar(mContext);
        progressBar.setVisibility(View.GONE);
        progressBar.setProgressTextColor(ContextCompat.getColor(mContext, R.color.progressBar));
        progressBar.setReachedBarColor(ContextCompat.getColor(mContext, R.color.progressBar));
        layout.addView(progressBar);

        container.addView(layout, 0);

        if (mShowProgressBar) {
            final ProgressTarget<String, Bitmap> target = new MyProgressTarget<>(new BitmapImageViewTarget(imageView), progressBar);
            target.setModel(imageURL);
            Glide.with(mContext).load(imageURL).asBitmap().thumbnail(thumbnailRequest).dontAnimate().into(target);
        } else {
            Glide.with(mContext).load(imageURL).asBitmap().thumbnail(thumbnailRequest).dontAnimate().into(imageView);
        }

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        container.removeView((RelativeLayout) obj);
    }

    private static class MyProgressTarget<Z> extends ProgressTarget<String, Z> {
        private final NumberProgressBar mProgressBar;
        private boolean mIsDownloadStarted;

        public MyProgressTarget(Target<Z> target, NumberProgressBar progress) {
            super(target);
            mProgressBar = progress;
        }

        @Override protected void onConnecting() {
        }

        @Override protected void onDownloading(long bytesRead, long expectedLength) {
            if (!mIsDownloadStarted) {
                mProgressBar.setVisibility(View.VISIBLE);
                mIsDownloadStarted = true;
            }
            mProgressBar.setProgress((int)(100 * bytesRead / expectedLength));
        }
        @Override protected void onDownloaded() {
        }
        @Override protected void onDelivered() {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}