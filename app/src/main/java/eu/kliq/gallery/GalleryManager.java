package eu.kliq.gallery;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import eu.kliq.gallery.json.JsonItem;

public class GalleryManager {

    public static final String BASE_URL = "http://kliq.eu/galeria2";
    public static final String ALBUMS_URL = BASE_URL + "/albums";
    public static final String JSON_URL = BASE_URL + "/data_v2.json";
    public static final String VERSION_URL = BASE_URL + "/version";

    private Random mRandomGenerator;
    private List<JsonItem> mItemList = new ArrayList<>();
    private List<JsonItem> mItemListFiltered = new ArrayList<>();
    private JsonItem mCurrentAlbum;
    private int mSortType;
    private OnListChangedListener mListChangedListener;
    private OnErrorListener mErrorListener;

    public GalleryManager() {
        mRandomGenerator = new Random();
    }

    public List<JsonItem> getAlbums() {
        return mItemListFiltered;
    }

    public JsonItem getAlbum(String name) {
        for (final JsonItem item : mItemList) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public void init(String itemsJson, OnListChangedListener listChangedListener, OnErrorListener errorListener,
                     int sortType) {
        mListChangedListener = listChangedListener;
        mErrorListener = errorListener;
        mSortType = sortType;
        final Gson gson = new Gson();
        final Type collectionType = new TypeToken<List<JsonItem>>(){}.getType();
        List<JsonItem> data = null;
        
        try {
            data = gson.fromJson(itemsJson, collectionType);
        } catch (JsonSyntaxException exception) {
            mErrorListener.onJsonParsed(false);
        }

        if (data == null || data.isEmpty()) {
            mErrorListener.onJsonParsed(false);
        } else {
            mErrorListener.onJsonParsed(true);
            buildList(data);
            sortItems(mItemList);
            mItemListFiltered.clear();
            mItemListFiltered.addAll(mItemList);
            if (mListChangedListener != null) {
                mListChangedListener.onListChanged();
            }
        }
    }

    private void buildList(List<JsonItem> list) {
        for (final JsonItem item : list) {
            Collections.sort(item.getImages());
            setItemData(item);
        }
        mItemList = list;
    }

    private void setItemData(JsonItem item) {
        final List<String> children = item.getImages();
        item.url = ALBUMS_URL + "/" + Uri.encode(item.getName());
        // set random image
        item.albumImage = children.get(mRandomGenerator.nextInt(children.size()));
    };

    public void setCurrentAlbum(JsonItem item) {
        mCurrentAlbum = item;
    }

    public void setCurrentAlbum(String name) {
        setCurrentAlbum(getAlbum(name));
    }

    public void setSortingType(int type) {
        if (type != mSortType) {
            mSortType = type;
            sortItems(mItemList);
            sortItems(mItemListFiltered);
            if (mListChangedListener != null) {
                mListChangedListener.onListChanged();
            }
        }
    }

    public void setFilter(String text) {
        mItemListFiltered.clear();
        if (text.isEmpty()) {
            mItemListFiltered.addAll(mItemList);
        } else {
            for (JsonItem item: mItemList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    mItemListFiltered.add(item);
                } else if (item.getTags() != null) {
                    for (String tag : item.getTags()) {
                        if (tag.toLowerCase().contains(text.toLowerCase())) {
                            mItemListFiltered.add(item);
                            break;
                        }
                    }
                }
            }
        }
        mListChangedListener.onSortChanged();
    }

    private void sortItems(List<JsonItem> list) {
        Collections.sort(list, new Comparator<JsonItem>() {
            @Override
            public int compare(JsonItem lhs, JsonItem rhs) {
                switch (mSortType) {
                    case SortingType.NAME_ASC:
                        return lhs.name.compareTo(rhs.name);
                    case SortingType.NAME_DESC:
                        return rhs.name.compareTo(lhs.name);
                    case SortingType.DATE_ASC:
                        return lhs.date.compareTo(rhs.date);
                    case SortingType.DATE_DESC:
                        return rhs.date.compareTo(lhs.date);
                    default:
                        return rhs.date.compareTo(lhs.date);
                }
            }
        });
    }

    public JsonItem getCurrentAlbum() {
        return mCurrentAlbum;
    }

}
