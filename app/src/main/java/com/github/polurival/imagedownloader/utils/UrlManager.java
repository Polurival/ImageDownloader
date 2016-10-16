package com.github.polurival.imagedownloader.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UrlManager {

    private static final String TAG = UrlManager.class.getSimpleName();

    private static final String BASE_URL
            = "https://placeholdit.imgix.net/~text?txtsize=15&w=120&h=120&txt=image";

    public static List<String> getUrls() {
        List<String> urls = new ArrayList<>();

        long tStart = System.currentTimeMillis();
        StringBuilder sb;
        for (int i = 1; i < 1000; i++) {
            sb = new StringBuilder();
            urls.add(sb.append(BASE_URL).append(i).toString());
        }
        long tEnd = System.currentTimeMillis();

        long elapsedMilliSeconds = (tEnd - tStart);
        Log.i(TAG, "getUrls() executing time in milliSeconds: " + elapsedMilliSeconds);

        return urls;
    }
}
