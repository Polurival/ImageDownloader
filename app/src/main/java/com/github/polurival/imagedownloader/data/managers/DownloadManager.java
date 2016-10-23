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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    //без этой Map наблюдается эффект, о котором мы говорили,
    //когда в один и тот же holder сначала загружается старое изображение, а затем новое
    private ConcurrentMap<ImageAdapter.Holder, String> mRequestMap;
    private ThreadPoolExecutor mExecutor;
    private ICache mMemoryCache;
    private final Handler uiHandler;

    private DownloadManager() {
        mRequestMap = new ConcurrentHashMap<>();
        mMemoryCache = App.getMemoryCache();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void initExecutor() {
        mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
    }

    public void startDownload(final ImageAdapter.Holder holder) {
        mRequestMap.put(holder, holder.getUrl());

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
            try {
                final String url = mRequestMap.get(mHolder);

                //иногда url = null
                if (url == null) {
                    return;
                }

                HttpURLConnection connection =
                        (HttpURLConnection) new URL(url).openConnection();
                final Bitmap bitmap = BitmapFactory
                        .decodeStream((InputStream) connection.getContent());
                Log.i(TAG, "Bitmap created");
                mMemoryCache.addBitmapToMemCache(url, bitmap);

                //предотвращает установку изображения не в свой holder
                if (!url.equals(mRequestMap.get(mHolder))) {
                    return;
                }

                setImageToHolder(bitmap);

            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading image", ioe);
            }
        }

        private void setImageToHolder(final Bitmap bitmap) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Current thread is: " + Thread.currentThread().getName());
                    mHolder.bindDrawable(bitmap);
                }
            });
        }
    }
}
