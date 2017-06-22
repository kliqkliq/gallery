package eu.kliq.gallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import eu.kliq.gallery.json.JsonItem;
import eu.kliq.gallery.R;

public class ImagesGridViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mLayoutResourceId;
    private List<String> mItems;

    public ImagesGridViewAdapter(Context context, int layoutResourceId, List<String> items) {
        super(context, layoutResourceId, items);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.thumbView = (ImageView) row.findViewById(R.id.thumb);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final String url = JsonItem.getThumbUrl(mItems.get(position));
        Glide.with(mContext).load(url).centerCrop().into(holder.thumbView);
        return row;
    }

    public class ViewHolder {
        public ImageView thumbView;
    }
}
