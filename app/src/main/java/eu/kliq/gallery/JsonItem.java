package eu.kliq.gallery;

import java.util.List;

class JsonItem {
    String name;
    List<JsonItem> children;
    String baseUrl;

    public String getName() {
        return name;
    }

    public List<JsonItem> getChildren() {
        return children;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getThumbUrl() {
        return getThumbUrl(baseUrl);
    }

    public String getImageUrl() {
        return getImageUrl(baseUrl);
    }

    public void setBaseUrl(String url) {
        baseUrl = url;
    }

    public static String getImageUrl(String url) {
        return url + ".jpg";
    }

    public static String getThumbUrl(String url) {
        return url + ".thumbnail";
    }
}
