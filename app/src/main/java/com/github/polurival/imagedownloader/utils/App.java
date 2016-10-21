package com.github.polurival.imagedownloader.utils;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.github.polurival.imagedownloader.data.managers.CacheManager;

/**
 * Created by Polurival
 * on 21.10.2016.
 */

public class App extends Application {

    private static LruCache<String, Bitmap> sMemoryCache;

    @Override
    public void onCreate() {
        super.onCreate();

        sMemoryCache = CacheManager.initMemoryCache();
    }

    public static LruCache<String, Bitmap> getMemoryCache() {
        return sMemoryCache;
    }
}
