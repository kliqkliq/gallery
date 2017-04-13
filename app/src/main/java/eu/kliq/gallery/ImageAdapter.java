package eu.kliq.gallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private ImageView mImageView;
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
        mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        container.addView(mImageView, 0);

        String baseUrl = mImages.get(i);
        String thumbURL = JsonItem.getThumbUrl(baseUrl);
        String imageURL = JsonItem.getImageUrl(baseUrl);

        DrawableRequestBuilder<String> thumbnailRequest = Glide.with(mContext).load(thumbURL);
        Glide.with(mContext).load(imageURL).thumbnail(thumbnailRequest).dontAnimate().into(mImageView);

        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        container.removeView((ImageView) obj);
    }
}