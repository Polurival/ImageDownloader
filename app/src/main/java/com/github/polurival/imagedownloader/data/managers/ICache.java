package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public interface ICache {

    LruCache<String, Bitmap> initMemoryCache();

    void addBitmapToMemCache(String url, Bitmap bitmap);

    Bitmap getBitmapFromMemCache(String url);
}
