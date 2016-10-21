package com.github.polurival.imagedownloader.utils;

public class UrlManager {

    private static final String BASE_URL
            = "https://placeholdit.imgix.net/~text?txtsize=15&w=120&h=120&txt=image";

    private UrlManager() {
    }

    public static String getUrl(int holderPosition) {
        return BASE_URL + String.valueOf(holderPosition + 1);
    }
}
