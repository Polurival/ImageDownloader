package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class MemoryCache implements ICache {

    private static final String TAG = MemoryCache.class.getSimpleName();

    private static ICache sInstance;

    public static ICache getInstance() {
        if (sInstance == null) {
            sInstance = new MemoryCache();
        }
        return sInstance;
    }

    private LruCache<String, Bitmap> mMemoryCache;

    private MemoryCache() {
        mMemoryCache = initMemoryCache();
    }

    private LruCache<String, Bitmap> initMemoryCache() {

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

    @Override
    public void addBitmapToMemCache(String url, Bitmap bitmap) {
        if (getBitmapFromMemCache(url) == null) {
            mMemoryCache.put(url, bitmap);
        }
    }

    @Override
    public Bitmap getBitmapFromMemCache(String url) {
        return mMemoryCache.get(url);
    }

}
