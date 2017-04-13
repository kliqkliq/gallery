package eu.kliq.gallery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import eu.kliq.gallery.AlbumsFragment.OnAlbumsFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link JsonItem} and makes a call to the
 * specified {@link OnAlbumsFragmentInteractionListener}.
 */
public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.ViewHolder> {

    private final List<JsonItem> mValues;
    private final OnAlbumsFragmentInteractionListener mListener;

    public AlbumsRecyclerViewAdapter(List<JsonItem> items, OnAlbumsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JsonItem item = mValues.get(position);
        holder.mItem = item;
        holder.mNameView.setText(item.getName());
        Glide.with(holder.mThumbView.getContext()).load(item.getThumbUrl()).centerCrop().into(holder.mThumbView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onAlbumInteraction(holder.mItem);
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
        public final TextView mNameView;
        public final ImageView mThumbView;
        public JsonItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mThumbView = (ImageView) view.findViewById(R.id.thumb);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
