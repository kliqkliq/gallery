package eu.kliq.gallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class ImagesFragment extends Fragment implements OnListChangedListener {

    private static final String ARG_ALBUM_NAME = "album-name";
    private String mAlbumName = "";
    private OnImagesFragmentInteractionListener mListener;
    private MainActivity mActivity;
    private ImagesGridViewAdapter mAdapter;
    private GridView mGridView;

    public ImagesFragment() {
    }

    public static ImagesFragment newInstance(String name) {
        ImagesFragment fragment = new ImagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        if (getArguments() != null) {
            mAlbumName = getArguments().getString(ARG_ALBUM_NAME, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragemnt_images, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     final JsonItem item = (JsonItem) parent.getItemAtPosition(position);
                     mListener.onImageInteraction(item);
                 }
             }
        );

        loadData();
        return view;
    }

    private void loadData() {
        mAdapter = new ImagesGridViewAdapter(mActivity, R.layout.image_item, mActivity.getAlbum(mAlbumName).getChildren());
        mGridView.setAdapter(mAdapter);
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
