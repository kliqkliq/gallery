package eu.kliq.gallery.json;

import java.util.ArrayList;
import java.util.List;

public class JsonItem {
    public String name;
    public String date = "2007-01-01";
    public List<String> images;
    public List<String> tags;
    public String url;
    public String albumImage;

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getUrl() {
        return url;
    }

    public String getAlbumImageUrl() {
        return getImageUrl(url + "/" + albumImage);
    }

    public String getAlbumThumbUrl() {
        return getThumbUrl(url + "/" + albumImage);
    }

    public String getImageBaseUrl(String name) {
        return url + "/" + name;
    }

    public List<String> getImageBaseUrlList() {
        final ArrayList<String> urls = new ArrayList<>();

        for (String image : getImages()) {
            urls.add(getImageBaseUrl(image));
        }

        return urls;
    }

    public static String getImageUrl(String url) {
        return url + ".jpg";
    }

    public static String getThumbUrl(String url) {
        return url + ".thumbnail";
    }
}
