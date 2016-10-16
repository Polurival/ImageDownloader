package com.github.polurival.imagedownloader.data.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Сделано на основе главы 24 из книги "Android. Программирование для профессионалов. 2-ое издание"
 */
public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = ThumbnailDownloader.class.getSimpleName();
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int HANDLER_COUNT = 4;

    // список Handler-ов привязанных к Looper-у ThumbnailDownloader
    private List<Handler> mHandlerList = new ArrayList<>();

    /* При прокрутке списка изображений, созданные Holder-ы переиспользуются,
       поэтому значение url в mRequestMap может быть перезаписано
       до того как скачанное изображение по старому url будет назначено ImageView Holder-a.
       Использование ConcurrentHashMap гарантирует получение правильного url */
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    /* Handler привязанный к Looper-у MainActivity,
       так как создается в ImageGalleryFragment
       и передается в ThumbnailDownloader в конструкторе */
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);

        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {

        for (int i = 0; i < HANDLER_COUNT; i++) {
            mHandlerList.add(new Handler() {
                // Обработка Message-а из очереди сообщений Looper-a ThumbnailDownloader-а
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MESSAGE_DOWNLOAD) {
                        T target = (T) msg.obj;
                        Log.i(TAG, this.toString()
                                + " Got a request for URL: " + mRequestMap.get(target));
                        handleRequest(target);
                    }
                }
            });
            Log.i(TAG, mHandlerList.get(i).toString() + " was added to List");
        }
    }

    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);

        if (url == null) {
            return;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            final Bitmap bitmap = BitmapFactory
                    .decodeStream((InputStream) connection.getContent());
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* Если сравнивать с помощью equals(),
                       то при быстром скроллинге вниз, а затем к самому началу списка
                       вылетает NullPointerException */
                    if (mRequestMap.get(target) != url) {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);

            Message message = Message.obtain();
            message.what = MESSAGE_DOWNLOAD;
            message.obj = target;

            Handler requestHandler = mHandlerList.remove(0);
            requestHandler.sendMessage(message);
            mHandlerList.add(requestHandler);
        }
    }

    /* Вызывается в onDestroyView() фрагмента,
       чтобы при перевороте экрана очистить очередь сообщений Looper.*/
    public void clearQueue() {
        for (int i = 0; i < HANDLER_COUNT; i++) {
            Handler requestHandler = mHandlerList.get(i);
            requestHandler.removeMessages(MESSAGE_DOWNLOAD);
        }
    }
}
