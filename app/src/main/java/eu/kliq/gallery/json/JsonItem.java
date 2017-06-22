package eu.kliq.gallery.json;

import java.util.ArrayList;
import java.util.List;

public class JsonItem {
    public String name;
    public String date;
    public List<String> children;
    public String url;
    public String albumImage;

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public List<String> getChildren() {
        return children;
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

        for (String image : getChildren()) {
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
