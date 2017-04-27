package eu.kliq.gallery;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GalleryManager {

    public static final String BASE_URL = "http://kliq.eu/galeria2";
    public static final String JSON_URL = BASE_URL + "/data.json";

    public GalleryManager() {
        mRandomGenerator = new Random();
    }

    private Random mRandomGenerator;
    private List<JsonItem> mItemList = new ArrayList<>();

    private JsonItem mCurrentAlbum;

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

    public void init(String itemsJson, OnListChangedListener listener) {
        final Gson gson = new Gson();
        final Type collectionType = new TypeToken<JsonItem>(){}.getType();
        final JsonItem data = gson.fromJson(itemsJson, collectionType);
        if (data != null) {
            mItemList = buildList(data, BASE_URL);
            if (listener != null) {
                listener.onListChanged();
            }
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

    public void setCurrentAlbum(JsonItem item) {
        mCurrentAlbum = item;
    }

    public JsonItem getCurrentAlbum() {
//        if (mCurrentAlbum == null) {
//            mCurrentAlbum = getAlbum(mCurrentAlbumName);
//        }
        return mCurrentAlbum;
    }
}
