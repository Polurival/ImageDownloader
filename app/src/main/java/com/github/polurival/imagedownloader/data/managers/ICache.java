package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;

public interface ICache {

    void addBitmapToMemCache(String url, Bitmap bitmap);

    Bitmap getBitmapFromMemCache(String url);
}
