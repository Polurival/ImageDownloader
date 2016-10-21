package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.polurival.imagedownloader.ui.adapters.ImageAdapter;

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
    private final Handler uiHandler;

    private DownloadManager() {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void initExecutor() {
        mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    }

    public void startDownload(final ImageAdapter.Holder holder) {

        DownloadTask downloadTask = new DownloadTask(holder);
        mExecutor.submit(downloadTask);
    }

    public void shutdownExecutor() {
        mExecutor.shutdown();
    }

    private class DownloadTask implements Runnable {

        private ImageAdapter.Holder mHolder;

        DownloadTask(ImageAdapter.Holder holder) {
            mHolder = holder;
        }

        @Override
        public void run() {
            Log.d(TAG, "Current thread is: " + Thread.currentThread().getName());

            final String url = mHolder.getUrl();

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                final Bitmap bitmap = BitmapFactory
                        .decodeStream((InputStream) connection.getContent());

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Current thread is: " + Thread.currentThread().getName());

                        CacheManager.addBitmapToMemCache(mHolder.getUrl(), bitmap);
                        mHolder.bindDrawable(bitmap);
                    }
                });

                Log.i(TAG, "Bitmap created");
            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading image", ioe);
            }
        }
    }
}
