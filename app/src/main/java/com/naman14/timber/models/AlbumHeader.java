package com.naman14.timber.models;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.naman14.timber.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by subhashacharya on 10/5/15.
 */
public class AlbumHeader {
    private String songTitle;
    private String songArtist;
    private long albumArt;

    public AlbumHeader() {
    }

    public AlbumHeader(String title, String artist, long art) {
        this.songTitle = title;
        this.songArtist = artist;
        this.albumArt = art;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public long getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(long albumArt) {
        this.albumArt = albumArt;
    }
}
