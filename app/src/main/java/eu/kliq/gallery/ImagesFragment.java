package eu.kliq.gallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ImagesFragment extends Fragment implements OnListChangedListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ALBUM_NAME = "album-name";
    private int mColumnCount = 3;
    private String mAlbumName = "";
    private OnImagesFragmentInteractionListener mListener;
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private ImagesRecyclerViewAdapter mAdapter;

    public ImagesFragment() {
    }

    public static ImagesFragment newInstance(int columnCount, String name) {
        ImagesFragment fragment = new ImagesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_ALBUM_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT, mColumnCount);
            mAlbumName = getArguments().getString(ARG_ALBUM_NAME, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            final Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        loadData();
        return view;
    }

    private void loadData() {
        mAdapter = new ImagesRecyclerViewAdapter(mActivity.getAlbum(mAlbumName).getChildren(), mListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImagesFragmentInteractionListener) {
            mListener = (OnImagesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnImagesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListChanged() {
       loadData();
    }

    public interface OnImagesFragmentInteractionListener {
        void onImageInteraction(JsonItem item);
    }
}
