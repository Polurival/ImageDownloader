package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.github.polurival.imagedownloader.utils.App;

public class CacheManager {

    private static final String TAG = CacheManager.class.getSimpleName();

    private CacheManager() {
    }

    public static LruCache<String, Bitmap> initMemoryCache() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        Log.i(TAG, "mMemoryCache in kilobytes: " + cacheSize);

        return new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemCache(String url, Bitmap bitmap) {
        if (getBitmapFromMemCache(url) == null) {
            App.getMemoryCache().put(url, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String url) {
        return App.getMemoryCache().get(url);
    }

}
