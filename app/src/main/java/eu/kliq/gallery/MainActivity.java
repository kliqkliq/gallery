package eu.kliq.gallery;

import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumsFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    private static final String ITEMS_JSON_KEY = "items-json-key";
    private static final String ALBUM_NAME_KEY = "album-name-key";
    private String mItemsJson;
    private GalleryManager mGalleryManager;
    private Toolbar mToolbar;
    private TextView mTitle;
    private AppBarLayout mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mAppBar = (AppBarLayout) findViewById(R.id.app_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.addOnBackStackChangedListener(this);

        mGalleryManager = new GalleryManager();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, AlbumsFragment.newInstance()).commit();
            final FetchDataTask task = new FetchDataTask();
            task.execute();
        } else {
            mItemsJson = savedInstanceState.getString(ITEMS_JSON_KEY);
            final OnListChangedListener listener = (OnListChangedListener) getCurrentFragment();
            mGalleryManager.init(mItemsJson, listener);
            final String name = savedInstanceState.getString(ALBUM_NAME_KEY);
            mGalleryManager.setCurrentAlbum(name);
            updateTitle();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ITEMS_JSON_KEY, mItemsJson);
        final JsonItem item = mGalleryManager.getCurrentAlbum();
        outState.putString(ALBUM_NAME_KEY, item == null ? "" : item.getName());
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void updateTitle() {
        final Fragment fragment = getCurrentFragment();
        if (fragment instanceof ImagesFragment) {
            setTitle(mGalleryManager.getCurrentAlbum().getName());
        } else {
            setTitle(getString(R.string.app_name));
        }
    }

    @Override
    public void onAlbumInteraction(JsonItem item) {
        mGalleryManager.setCurrentAlbum(item);
        final Fragment fragment = ImagesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
    }

    @Override
    public void onBackStackChanged() {
        updateTitle();
        mAppBar.setExpanded(true);
    }

    public GalleryManager getGalleryManager() {
        return mGalleryManager;
    }

    public class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return JsonHelper.getJSON(GalleryManager.JSON_URL);
        }

        @Override
        protected void onPostExecute(String result) {
            mItemsJson = result;
            final OnListChangedListener listener = (OnListChangedListener) getCurrentFragment();
            mGalleryManager.init(mItemsJson, listener);
        }
    }
}
