package eu.kliq.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class ImagesFragment extends Fragment implements OnListChangedListener {

    private static final String ARG_ALBUM_NAME = "album-name";
    private String mAlbumName = "";
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

                     final Intent intent = new Intent(mActivity, ImageActivity.class);
                     final List<JsonItem> images =  mActivity.getAlbum(mAlbumName).getChildren();
                     final ArrayList<String> urls = new ArrayList<>();

                     for (JsonItem image : images) {
                         urls.add(image.getBaseUrl());
                     }

                     intent.putExtra("position", position).putStringArrayListExtra("images", urls);

                     startActivity(intent);
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
    public void onListChanged() {
        loadData();
    }
}
