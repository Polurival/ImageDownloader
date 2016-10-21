package com.github.polurival.imagedownloader.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.polurival.imagedownloader.R;
import com.github.polurival.imagedownloader.data.managers.DownloadManager;
import com.github.polurival.imagedownloader.ui.adapters.ImageAdapter;

/**
 * todo:
 * android приложение - состоящее из одного экрана,
 * на экране список (или grid на выбор) картинок который можно скролить от 1 до 10000 картинки.
 * сделать кеш картинок в памяти.
 * Загружать картинки по мере скролла, отменять загрузку если картинка уже не видна
 * - те максимально быстро показывать картинки которые сейчас видны.
 * Иметь ограничение количества одновременно загружаемых картинок N = 4.
 * Не использовать сторонние библиотеки для загрузки картинок.
 * <p>
 * Картинки загружать по url
 * 1 -   https://placeholdit.imgix.net/~text?txtsize=15&txt=image1&w=120&h=120
 * ....
 * 999 - https://placeholdit.imgix.net/~text?txtsize=15&txt=image999&w=120&h=120
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();

        DownloadManager.getInstance().shutdownExecutor();
    }

    private void initRecyclerView() {
        RecyclerView mImageRecyclerView =
                (RecyclerView) findViewById(R.id.fragment_image_gallery_recycler_view);
        mImageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        ImageAdapter adapter = new ImageAdapter(this);
        mImageRecyclerView.setAdapter(adapter);
    }
}
