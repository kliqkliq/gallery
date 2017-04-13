package eu.kliq.gallery;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumsFragmentInteractionListener{

    private static final String BASE_URL = "http://kliq.eu/galeria2";
    private static final String JSON_URL = BASE_URL + "/data.json";
    private List<JsonItem> mItemList = new ArrayList<>();
    private Random mRandomGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRandomGenerator = new Random();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, AlbumsFragment.newInstance()).commit();
        }
        final FetchDataTask task = new FetchDataTask();
        task.execute();
    }

    public List<JsonItem> getAlbums() {
        return mItemList;
    }

    public JsonItem getAlbum(String name) {
        for (final JsonItem item : mItemList) {
            if (item.getName() == name) {
                return item;
            }
        }
        return null;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.container);
    }

    private List<JsonItem> buildList(JsonItem data, String url) {
        final List<JsonItem> list = data.getChildren();
        String baseUrl = url + "/" + Uri.encode(data.getName());
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
        final Fragment fragment = ImagesFragment.newInstance(item.getName());
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack("").commit();
    }

    public class FetchDataTask extends AsyncTask<Object, Object, JsonItem> {

        @Override
        protected JsonItem doInBackground(Object... params) {
            final String data = JsonHelper.getJSON(JSON_URL);
            final Gson gson = new Gson();
            final Type collectionType = new TypeToken<JsonItem>(){}.getType();
            return gson.fromJson(data, collectionType);
        }

        @Override
        protected void onPostExecute(JsonItem result) {
            if (result != null) {
                mItemList = buildList(result, BASE_URL);
                final OnListChangedListener fragment = (OnListChangedListener) getCurrentFragment();
                fragment.onListChanged();
            }
        }
    }
}
