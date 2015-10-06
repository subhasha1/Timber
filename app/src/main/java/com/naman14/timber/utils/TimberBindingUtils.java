package com.naman14.timber.utils;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.naman14.timber.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by subhashacharya on 10/6/15.
 */
public class TimberBindingUtils {
    @BindingAdapter({"bind:albumArt", "bind:bitmapDisplayer"})
    public static void displayAlbumArt(ImageView view, long artId, int displayerDelay) {
        displayAlbumArt(view, TimberUtils.getAlbumArtUri(artId).toString(), displayerDelay);
    }

    @BindingAdapter({"bind:albumArt", "bind:bitmapDisplayer"})
    public static void displayAlbumArt(ImageView view, String url, int displayerDelay) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnFail(R.drawable.ic_empty_music2)
                .resetViewBeforeLoading(true);
        if (displayerDelay > 0) {
            builder.displayer(new FadeInBitmapDisplayer(displayerDelay));
        }
        ImageLoader.getInstance().displayImage(url, view, builder.build());
    }

    @BindingAdapter("bind:albumArt")
    public static void displayAlbumArt(ImageView view, long artId) {
        displayAlbumArt(view, artId, 0);
    }

    @BindingAdapter("bind:albumArt")
    public static void displayAlbumArt(ImageView view, String url) {
        displayAlbumArt(view, url, 0);
    }

    @BindingAdapter("bind:itemDecoration")
    public static void setItemDecoration(RecyclerView view, RecyclerView.ItemDecoration decoration) {
        if (decoration != null)
            view.addItemDecoration(decoration);
    }
}
