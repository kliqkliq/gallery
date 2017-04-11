package eu.kliq.gallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlbumsFragment extends Fragment implements OnListChangedListener {

    private OnAlbumsFragmentInteractionListener mListener;
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private AlbumsRecyclerViewAdapter mAdapter;

    public AlbumsFragment() {
    }

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            final Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        loadData();
        return view;
    }

    private void loadData() {
        mAdapter = new AlbumsRecyclerViewAdapter(mActivity.getAlbums(), mListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAlbumsFragmentInteractionListener) {
            mListener = (OnAlbumsFragmentInteractionListener) context;
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

    public interface OnAlbumsFragmentInteractionListener {
        void onAlbumInteraction(JsonItem item);
    }
}
