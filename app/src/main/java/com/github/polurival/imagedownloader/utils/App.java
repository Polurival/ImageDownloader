package com.github.polurival.imagedownloader.utils;

import android.app.Application;

import com.github.polurival.imagedownloader.data.managers.ICache;
import com.github.polurival.imagedownloader.data.managers.MemoryCache;

public class App extends Application {

    private static ICache sMemoryCache;

    @Override
    public void onCreate() {
        super.onCreate();

        sMemoryCache = MemoryCache.getInstance();
    }

    public static ICache getMemoryCache() {
        return sMemoryCache;
    }
}
