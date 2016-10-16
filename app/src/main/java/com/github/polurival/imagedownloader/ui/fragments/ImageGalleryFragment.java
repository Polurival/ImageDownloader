package com.github.polurival.imagedownloader.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.polurival.imagedownloader.ui.adapters.ImageAdapter;
import com.github.polurival.imagedownloader.R;
import com.github.polurival.imagedownloader.data.managers.ThumbnailDownloader;

public class ImageGalleryFragment extends Fragment {

    private static final String TAG = ImageGalleryFragment.class.getSimpleName();

    private RecyclerView mImageRecyclerView;
    private LruCache<String, Bitmap> mMemoryCache;

    private ThumbnailDownloader<ImageAdapter.Holder> mThumbnailDownloader;

    public static ImageGalleryFragment newInstance() {
        return new ImageGalleryFragment();
    }

    public ImageGalleryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* При пересоздании активности в результате изменения конфигурации (поворот экрана),
           фрагмент отсоединяется (сработает onDestroyView() и onDetach()), от активности-хоста,
           а затем присоединяется. При этом у фрагмента сработают методы onAttach()
           и onCreateView(), а onCreate(...) не сработает,
           то есть заново создается только представление фрагмента */
        setRetainInstance(true);

        initBackgroundThread();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_gallery, container, false);

        mImageRecyclerView
                = (RecyclerView) v.findViewById(R.id.fragment_image_gallery_recycler_view);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void initBackgroundThread() {
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<ImageAdapter.Holder>() {
                    @Override
                    public void onThumbnailDownloaded(ImageAdapter.Holder holder, Bitmap bitmap) {
                        holder.bindDrawable(bitmap, holder.getAdapterPosition());
                    }
                }
        );

        /* вызовы start() и getLooper() в данной последовательности обеспечивают гарантию
           того, что до вызова метода ThumbnailDownloader.queueThumbnail()
           сработает метод ThumbnailDownloader.onLooperPrepared()
           и инициализируются Handler-ы, которые обрабатывают Message-ы,
           отправляемые внутри ThumbnailDownloader.queueThumbnail() */
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    private void setupAdapter() {
        if (isAdded()) {
            initMemoryCache();

            ImageAdapter adapter
                    = new ImageAdapter(getActivity(), mThumbnailDownloader, mMemoryCache);
            mImageRecyclerView.setAdapter(adapter);
        }
    }

    private void initMemoryCache() {
        if (mMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            Log.i(TAG, "mMemoryCache in kilobytes: " + cacheSize);

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
    }
}
