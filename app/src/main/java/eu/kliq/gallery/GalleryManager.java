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

import eu.kliq.gallery.json.JsonItem;

public class GalleryManager {

    public static final String BASE_URL = "http://kliq.eu/galeria2";
    public static final String ALBUMS_URL = BASE_URL + "/albums";
    public static final String JSON_URL = BASE_URL + "/data.json";

    private Random mRandomGenerator;
    private List<JsonItem> mItemList = new ArrayList<>();
    private List<JsonItem> mItemListFiltered = new ArrayList<>();
    private JsonItem mCurrentAlbum;
    private int mSortType;
    private OnListChangedListener mListener;

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

    public void init(String itemsJson, OnListChangedListener listener, int sortType) {
        mListener = listener;
        mSortType = sortType;
        final Gson gson = new Gson();
        final Type collectionType = new TypeToken<List<JsonItem>>(){}.getType();
        final List<JsonItem> data = gson.fromJson(itemsJson, collectionType);
        if (data != null) {
            mItemList = buildList(data);
            mItemListFiltered = new ArrayList<>();
            mItemListFiltered.addAll(mItemList);
            sortItems();
            if (mListener != null) {
                mListener.onListChanged();
            }
        }
    }

    private List<JsonItem> buildList(List<JsonItem> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<JsonItem>() {
                @Override
                public int compare(JsonItem lhs, JsonItem rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });
            for (final JsonItem item : list) {
                setItemData(item);
            }
        }
        return list;
    }

    private void setItemData(JsonItem item) {
        final List<String> children = item.getChildren();
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
            sortItems();
            mListener.onSortChanged();
        }
    }

    public void setFilter(String text) {
        mItemListFiltered.clear();
        if (text.isEmpty()) {
            mItemListFiltered.addAll(mItemList);
        } else {
            for (JsonItem item: mItemList){
                if (item.getName().toLowerCase().contains(text.toLowerCase())){
                    mItemListFiltered.add(item);
                }
            }
        }
        mListener.onSortChanged();
    }

    private void sortItems() {
        Collections.sort(mItemListFiltered, new Comparator<JsonItem>() {
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
