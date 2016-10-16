package com.github.polurival.imagedownloader.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.github.polurival.imagedownloader.R;
import com.github.polurival.imagedownloader.ui.fragments.ImageGalleryFragment;

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
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            /* В данном случае можно просто new ImageGalleryFragment().
               В newInstance() можно добавить параметры, которые добавляются в Bundle.
               Bundle добавляется к фрагменту с помощью setArguments()
               Этот механизм позволяет использовать один класс фрагмента для разных данных.
             */
            fragment = ImageGalleryFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
