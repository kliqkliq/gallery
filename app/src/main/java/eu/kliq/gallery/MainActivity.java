package eu.kliq.gallery;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumsFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String ITEMS_JSON_KEY = "items-json-key";
    private static final String ALBUM_NAME_KEY = "album-name-key";
    private String mItemsJson;
    private GalleryManager mGalleryManager;
    private Toolbar mToolbar;
    private TextView mTitle;
    private AppBarLayout mAppBar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mAppBar = (AppBarLayout) findViewById(R.id.app_bar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        View header = mNavigationView.getHeaderView(0);
        TextView appNameAndVersion = (TextView) header.findViewById(R.id.app_name_version);
        appNameAndVersion.setText(getString(R.string.version_name, getString(R.string.app_name), BuildConfig.VERSION_NAME));

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public GalleryManager getGalleryManager() {
        return mGalleryManager;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
