package eu.kliq.gallery;

import java.util.List;

class JsonItem {
    String name;
    List<JsonItem> children;
    String thumbUrl;
    String imageUrl;

    public String getName() {
        return name;
    }

    public List<JsonItem> getChildren() {
        return children;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setThumbUrl(String url) {
        thumbUrl = url;
    }

    public void setImageUrl(String url) {
        imageUrl = url;
    }
}
