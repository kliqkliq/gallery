package eu.kliq.gallery;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GalleryManager {

    public static final String BASE_URL = "http://kliq.eu/galeria2";
    public static final String JSON_URL = BASE_URL + "/data.json";

    public enum SORT_TYPE {
        NAME_ASC, NAME_DESC, DATE_ASC, DATE_DESC
    }

    public GalleryManager() {
        mRandomGenerator = new Random();
    }

    private Random mRandomGenerator;
    private List<JsonItem> mItemList = new ArrayList<>();

    private JsonItem mCurrentAlbum;

    public List<JsonItem> getAlbums() {
        return mItemList;
    }

    public List<JsonItem> getAlbums(final SORT_TYPE type) {
        final List<JsonItem> items = mItemList;
        Collections.sort(items, new Comparator<JsonItem>() {
            @Override
            public int compare(JsonItem lhs, JsonItem rhs) {
                switch (type) {
                    case NAME_ASC:
                        return lhs.name.compareTo(rhs.name);
                    case NAME_DESC:
                        return rhs.name.compareTo(lhs.name);
                    case DATE_ASC:
                        return lhs.date.compareTo(rhs.date);
                    case DATE_DESC:
                        return rhs.date.compareTo(lhs.date);
                    default:
                        return rhs.date.compareTo(lhs.date);
                }
            }
        });
        return items;
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
            Collections.sort(list, new Comparator<JsonItem>() {
                @Override
                public int compare(JsonItem lhs, JsonItem rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });
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

    public void setCurrentAlbum(String name) {
        setCurrentAlbum(getAlbum(name));
    }

    public JsonItem getCurrentAlbum() {
        return mCurrentAlbum;
    }

}
