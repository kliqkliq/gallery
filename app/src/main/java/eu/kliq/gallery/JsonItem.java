package eu.kliq.gallery;

import java.util.List;

class JsonItem {
    String name;
    List<JsonItem> children;
    String mUrl;

    public String getName() {
        return name;
    }

    public List<JsonItem> getChildren() {
        return children;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
