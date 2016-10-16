package com.github.polurival.imagedownloader.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.polurival.imagedownloader.R;
import com.github.polurival.imagedownloader.data.managers.ThumbnailDownloader;
import com.github.polurival.imagedownloader.utils.UrlManager;

import java.util.List;

/**
 * MemoryCache:
 * https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 * http://startandroid.ru/ru/uroki/vse-uroki-spiskom/376-urok-161-risovanie-bitmap-memory-kesh-picasso.html
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {

    private Context mContext;
    private ThumbnailDownloader<Holder> mThumbnailDownloader;
    private LruCache<String, Bitmap> mMemoryCache;
    private List<String> mUrls;

    public ImageAdapter(Context context,
                        ThumbnailDownloader<Holder> thumbnailDownloader,
                        LruCache<String, Bitmap> memoryCache) {
        mContext = context;
        mThumbnailDownloader = thumbnailDownloader;
        mMemoryCache = memoryCache;
        mUrls = UrlManager.getUrls();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView mItemImageView;

        private Holder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawable(Bitmap bitmap, int position) {
            addBitmapToMemoryCache(String.valueOf(position), bitmap);

            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            mItemImageView.setImageDrawable(drawable);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Bitmap bitmap = getBitmapFromMemCache(String.valueOf(position));
        if (bitmap == null) {

            Drawable placeHolder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                placeHolder = mContext.getDrawable(R.drawable.ic_star_border_black_24dp);
            } else {
                placeHolder
                        = mContext.getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
            }
            holder.mItemImageView.setImageDrawable(placeHolder);

            String url = mUrls.get(position);
            mThumbnailDownloader.queueThumbnail(holder, url);
        } else {
            holder.bindDrawable(bitmap, position);
        }
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
