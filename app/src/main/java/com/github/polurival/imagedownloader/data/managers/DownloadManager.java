package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.polurival.imagedownloader.ui.adapters.ImageAdapter;
import com.github.polurival.imagedownloader.utils.App;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadManager {

    private static final String TAG = DownloadManager.class.getSimpleName();

    private static DownloadManager sInstance;

    public static DownloadManager getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadManager();
        }
        return sInstance;
    }

    private ThreadPoolExecutor mExecutor;
    private ICache mMemoryCache;
    private final Handler uiHandler;

    private DownloadManager() {
        mMemoryCache = App.getMemoryCache();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void initExecutor() {
        mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    }

    public void shutdownExecutor() {
        mExecutor.shutdown();
    }

    public void startDownload(final ImageAdapter.Holder holder) {
        DownloadTask downloadTask = new DownloadTask(holder);
        mExecutor.submit(downloadTask);
    }

    private class DownloadTask implements Runnable {

        private ImageAdapter.Holder mHolder;

        DownloadTask(ImageAdapter.Holder holder) {
            mHolder = holder;
        }

        @Override
        public void run() {
            Log.d(TAG, "Current thread is: " + Thread.currentThread().getName());
            try {
                String url = mHolder.getUrl();

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                final Bitmap bitmap = BitmapFactory
                        .decodeStream((InputStream) connection.getContent());
                Log.i(TAG, "Bitmap created");

                mMemoryCache.addBitmapToMemCache(url, bitmap);

                setImageToHolder(url, bitmap);

            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading image", ioe);
            }
        }

        private void setImageToHolder(final String url, final Bitmap bitmap) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Current thread is: " + Thread.currentThread().getName());

                    if (url.equals(mHolder.getUrl())) {
                        mHolder.bindDrawable(bitmap);
                    }
                }
            });
        }
    }
}
