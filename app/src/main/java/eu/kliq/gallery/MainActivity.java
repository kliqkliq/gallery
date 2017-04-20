package eu.kliq.gallery;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumsFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    private static final String BASE_URL = "http://kliq.eu/galeria2";
    private static final String JSON_URL = BASE_URL + "/data.json";
    private static final String ITEMS_JSON_KEY = "items-json-key";
    private static final String ALBUM_NAME_KEY = "album-name-key";
    private String mItemsJson;
    private String mCurrentAlbumName;
    private List<JsonItem> mItemList = new ArrayList<>();
    private JsonItem mCurrentAlbum;
    private Random mRandomGenerator;
    private Toolbar mToolbar;
    private TextView mTitle;
    private AppBarLayout mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRandomGenerator = new Random();
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mAppBar = (AppBarLayout) findViewById(R.id.app_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, AlbumsFragment.newInstance()).commit();
            final FetchDataTask task = new FetchDataTask();
            task.execute();
        } else {
            mCurrentAlbumName = savedInstanceState.getString(ALBUM_NAME_KEY);
            mItemsJson = savedInstanceState.getString(ITEMS_JSON_KEY);
            updateTitle();
            generateJsonItem();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ITEMS_JSON_KEY, mItemsJson);
        outState.putString(ALBUM_NAME_KEY, mCurrentAlbumName);
    }

    public List<JsonItem> getAlbums() {
        return mItemList;
    }

    public JsonItem getAlbum(String name) {
        for (final JsonItem item : mItemList) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public JsonItem getCurrentAlbum() {
        if (mCurrentAlbum == null) {
            mCurrentAlbum = getAlbum(mCurrentAlbumName);
        }
        return mCurrentAlbum;
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
            setTitle(mCurrentAlbumName);
        } else {
            setTitle(getString(R.string.app_name));
        }
    }

    private void generateJsonItem() {
        final Gson gson = new Gson();
        final Type collectionType = new TypeToken<JsonItem>(){}.getType();
        final JsonItem data = gson.fromJson(mItemsJson, collectionType);
        if (data != null) {
            mItemList = buildList(data, BASE_URL);
            final OnListChangedListener fragment = (OnListChangedListener) getCurrentFragment();
            fragment.onListChanged();
        }
    }

    private List<JsonItem> buildList(JsonItem data, String url) {
        final List<JsonItem> list = data.getChildren();
        final String baseUrl = url + "/" + Uri.encode(data.getName());
        data.setBaseUrl(baseUrl);

        if (list != null) {
            for (final JsonItem item : list) {
                buildList(item, baseUrl);
            }
            // set random thumb
            data.setBaseUrl(list.get(mRandomGenerator.nextInt(list.size())).getBaseUrl());
        }
        return list;
    }

    @Override
    public void onAlbumInteraction(JsonItem item) {
        mCurrentAlbum = item;
        mCurrentAlbumName = item.getName();
        final Fragment fragment = ImagesFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
    }

    @Override
    public void onBackStackChanged() {
        updateTitle();
        mAppBar.setExpanded(true);
    }

    public class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return JsonHelper.getJSON(JSON_URL);
        }

        @Override
        protected void onPostExecute(String result) {
            mItemsJson = result;
            generateJsonItem();
        }
    }
}
