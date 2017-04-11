package eu.kliq.gallery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import eu.kliq.gallery.ImagesFragment.OnImagesFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link JsonItem} and makes a call to the
 * specified {@link OnImagesFragmentInteractionListener}.
 */
public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter<ImagesRecyclerViewAdapter.ViewHolder> {

    private final List<JsonItem> mValues;
    private final OnImagesFragmentInteractionListener mListener;

    public ImagesRecyclerViewAdapter(List<JsonItem> items, OnImagesFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_albums, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JsonItem item = mValues.get(position);
        holder.mItem = item;
        Glide.with(holder.mThumbView.getContext()).load(item.getUrl()).into(holder.mThumbView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onImageInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumbView;
        public JsonItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbView = (ImageView) view.findViewById(R.id.thumb);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getName() + "'";
        }
    }
}
