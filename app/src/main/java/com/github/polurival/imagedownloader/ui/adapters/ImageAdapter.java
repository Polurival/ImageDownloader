package com.github.polurival.imagedownloader.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.polurival.imagedownloader.R;
import com.github.polurival.imagedownloader.data.managers.DownloadManager;
import com.github.polurival.imagedownloader.data.managers.ICache;
import com.github.polurival.imagedownloader.utils.App;
import com.github.polurival.imagedownloader.utils.UrlManager;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {

    //Так как теперь в адаптере нет коллекции url-ов, количество item-ов пришлось захардкодить
    private static final int ITEM_COUNT = 1000;

    private Context mContext;
    private DownloadManager mDownloadManager;
    private ICache mMemoryCache;

    public ImageAdapter(Context context) {
        mContext = context;
        mDownloadManager = DownloadManager.getInstance();
        mMemoryCache = App.getMemoryCache();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.mUrl = UrlManager.getUrl(position);

        Bitmap bitmap = mMemoryCache.getBitmapFromMemCache(holder.getUrl());
        if (bitmap == null) {

            setPlaceHolder(holder);

            mDownloadManager.startDownload(holder);
        } else {
            holder.bindDrawable(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    private void setPlaceHolder(Holder holder) {
        Drawable placeHolder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            placeHolder = mContext.getDrawable(R.drawable.ic_star_border_black_24dp);
        } else {
            placeHolder = mContext.getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        }
        holder.mItemImageView.setImageDrawable(placeHolder);
    }

    public class Holder extends RecyclerView.ViewHolder {

        private String mUrl;
        private ImageView mItemImageView;

        private Holder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public String getUrl() {
            return mUrl;
        }

        public void bindDrawable(Bitmap bitmap) {
            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
            mItemImageView.setImageDrawable(drawable);
        }
    }
}
