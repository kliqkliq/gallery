package eu.kliq.gallery.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import eu.kliq.gallery.BuildConfig;
import eu.kliq.gallery.GalleryManager;
import eu.kliq.gallery.OnErrorListener;
import eu.kliq.gallery.json.JsonHelper;
import eu.kliq.gallery.json.JsonItem;
import eu.kliq.gallery.OnListChangedListener;
import eu.kliq.gallery.R;
import eu.kliq.gallery.SortingType;
import eu.kliq.gallery.fragment.AlbumsFragment;
import eu.kliq.gallery.fragment.ImagesFragment;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumsFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, CompoundButton.OnCheckedChangeListener, OnErrorListener {

    public static final String ITEMS_JSON_KEY = "items-json-key";
    public static final String ITEMS_VERSION_KEY = "items-version-key";
    public static final String ALBUM_NAME_KEY = "album-name-key";
    public static final String PREFS_FILE_NAME = "gallery_prefs";
    public static final String PREF_SORTING_TYPE_KEY = "sorting-type-key";
    public static final String PREF_SHOW_PROGRESS_BAR_KEY = "show-progress-bar-key";
    public static final int DEFAULT_SORT_TYPE = SortingType.DATE_DESC;
    public static final boolean DEFAULT_PROGRESS_BAR_STATUS = true;
    private String mItemsJson;
    private String mItemsVersion;
    private GalleryManager mGalleryManager;
    private Toolbar mToolbar;
    private TextView mTitle;
    private AppBarLayout mAppBar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private SearchView mSearchView;
    private FrameLayout mContainerLayout;
    private FrameLayout mErrorLayout;
    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mAppBar = (AppBarLayout) findViewById(R.id.app_bar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mSearchView = (SearchView) findViewById(R.id.search);
        mContainerLayout = (FrameLayout) findViewById(R.id.container);
        mErrorLayout = (FrameLayout) findViewById(R.id.error);
        mRetryButton = (Button) findViewById(R.id.retry_button);

        setNavigationDrawer();
        setSearchViewListeners();
        setOnClickListeners();

        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.addOnBackStackChangedListener(this);

        mGalleryManager = new GalleryManager();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, AlbumsFragment.newInstance()).commit();
            fetchData();
        } else {
            mItemsJson = savedInstanceState.getString(ITEMS_JSON_KEY);
            mItemsVersion = savedInstanceState.getString(ITEMS_VERSION_KEY);
            final OnListChangedListener listener = (OnListChangedListener) getCurrentFragment();
            mGalleryManager.init(mItemsJson, listener, this, getSortType());
            final String name = savedInstanceState.getString(ALBUM_NAME_KEY);
            mGalleryManager.setCurrentAlbum(name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTitle();
        fetchVersion();
    }

    private void setNavigationDrawer() {
        // Set header
        final View header = mNavigationView.getHeaderView(0);
        final TextView appNameAndVersion = (TextView) header.findViewById(R.id.app_name_version);
        appNameAndVersion.setText(getString(R.string.version_name, BuildConfig.VERSION_NAME));

        // Set sorting group header
        final MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.sorting);
        final SpannableString string = new SpannableString(menuItem.getTitle());
        string.setSpan(new TextAppearanceSpan(this, R.style.MenuGroupNameTextAppearance), 0, string.length(), 0);
        menuItem.setTitle(string);

        // Highlight sort type
        int sortTypeId;
        switch (getSortType()) {
            case SortingType.DATE_ASC: {
                sortTypeId = R.id.sort_by_date_asc;
                break;
            }
            case SortingType.DATE_DESC: {
                sortTypeId = R.id.sort_by_date_desc;
                break;
            }
            case SortingType.NAME_ASC: {
                sortTypeId = R.id.sort_by_name_asc;
                break;
            }
            case SortingType.NAME_DESC: {
                sortTypeId = R.id.sort_by_name_desc;
                break;
            }
            default:
                sortTypeId = R.id.sort_by_date_desc;
                break;
        }
        mNavigationView.setCheckedItem(sortTypeId);

        // Set progress bar switch listener
        final MenuItem switchItem = mNavigationView.getMenu().findItem(R.id.progress);
        final CompoundButton switchView = (CompoundButton) MenuItemCompat.getActionView(switchItem);
        switchView.setOnCheckedChangeListener(this);

        // Set progress bar switch state
        final SharedPreferences sharedPref = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final boolean isShow = sharedPref.getBoolean(PREF_SHOW_PROGRESS_BAR_KEY, DEFAULT_PROGRESS_BAR_STATUS);
        switchView.setChecked(isShow);

        // Set drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setSearchViewListeners() {
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle.setVisibility(View.GONE);
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mTitle.setVisibility(View.VISIBLE);
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(this);
    }

    private void setOnClickListeners() {
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ITEMS_JSON_KEY, mItemsJson);
        final JsonItem item = mGalleryManager.getCurrentAlbum();
        outState.putString(ALBUM_NAME_KEY, item == null ? "" : item.getName());
    }

    private void fetchData() {
        final FetchDataTask task = new FetchDataTask();
        task.execute();
    }

    private void fetchVersion() {
        final FetchVersionTask task = new FetchVersionTask();
        task.execute();
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
            mTitle.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.GONE);
        } else if (fragment instanceof AlbumsFragment) {
            setTitle(getString(R.string.app_name));
            if (!mSearchView.isIconified()) {
                mTitle.setVisibility(View.GONE);
            }
        }
    }

    private int getSortType() {
        final SharedPreferences sharedPref = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(PREF_SORTING_TYPE_KEY, DEFAULT_SORT_TYPE);
    }

    @Override
    public void onAlbumInteraction(JsonItem item) {
        mGalleryManager.setCurrentAlbum(item);
        final Fragment fragment = ImagesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
        mTitle.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
    }

    @Override
    public void onBackStackChanged() {
        updateTitle();
        mAppBar.setExpanded(true);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mSearchView.setVisibility(View.VISIBLE);
            if (mSearchView.getQuery().length() > 0) {
                mTitle.setVisibility(View.GONE);
                mSearchView.clearFocus();
            } else {
                mSearchView.setIconified(true);
            }
        }
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
        final int sortType;

        switch (id) {
            case R.id.sort_by_date_asc: {
                sortType = SortingType.DATE_ASC;
                break;
            }
            case R.id.sort_by_date_desc: {
                sortType = SortingType.DATE_DESC;
                break;
            }
            case R.id.sort_by_name_asc: {
                sortType = SortingType.NAME_ASC;
                break;
            }
            case R.id.sort_by_name_desc: {
                sortType = SortingType.NAME_DESC;
                break;
            }
            default:
                sortType = DEFAULT_SORT_TYPE;
                break;
        }

        mGalleryManager.setSortingType(sortType);

        final SharedPreferences sharedPref = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PREF_SORTING_TYPE_KEY, sortType);
        editor.apply();

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        mGalleryManager.setFilter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mGalleryManager.setFilter(newText);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final SharedPreferences sharedPref = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(PREF_SHOW_PROGRESS_BAR_KEY, isChecked);
        editor.apply();
    }

    @Override
    public void onJsonParsed(boolean isSuccess) {
        mContainerLayout.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        mErrorLayout.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
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
            mGalleryManager.init(mItemsJson, listener, MainActivity.this, getSortType());
        }
    }

    public class FetchVersionTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return JsonHelper.getJSON(GalleryManager.VERSION_URL);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals(mItemsVersion)) {
                if (mItemsVersion != null) {
                    fetchData();
                }
                mItemsVersion = result;

            }
        }
    }
}
