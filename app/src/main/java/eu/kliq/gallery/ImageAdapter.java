package eu.kliq.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
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

public class ImageAdapter extends PagerAdapter {

    public static final String LOG_TAG = "ImageAdapter";

    private Context mContext;
    private ArrayList<String> mImages;

    ImageAdapter(Context context, ArrayList<String> images) {
        mContext = context;
        mImages = images;
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
        layout.addView(progressBar);

        container.addView(layout, 0);

        final ProgressTarget<String, Bitmap> target = new MyProgressTarget<>(new BitmapImageViewTarget(imageView), progressBar);
        target.setModel(imageURL);
        Glide.with(mContext).load(imageURL).asBitmap().thumbnail(thumbnailRequest).dontAnimate().into(target);

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        container.removeView((RelativeLayout) obj);
    }

    private static class MyProgressTarget<Z> extends ProgressTarget<String, Z> {
        private final NumberProgressBar progress;
        public MyProgressTarget(Target<Z> target, NumberProgressBar progress) {
            super(target);
            this.progress = progress;
        }

        @Override public float getGranualityPercentage() {
            return 0.1f; // this matches the format string for #text below
        }

        @Override protected void onConnecting() {
            progress.setVisibility(View.VISIBLE);
        }
        @Override protected void onDownloading(long bytesRead, long expectedLength) {
            progress.setProgress((int)(100 * bytesRead / expectedLength));
        }
        @Override protected void onDownloaded() {
        }
        @Override protected void onDelivered() {
            progress.setVisibility(View.GONE);
        }
    }
}