/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.BR;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.ArtistQuery;
import com.naman14.timber.lastfmapi.models.LastfmArtist;
import com.naman14.timber.models.Album;
import com.naman14.timber.models.Artist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {

    private Activity mContext;
    private List searchResults = Collections.emptyList();

    public SearchAdapter(Activity context) {
        this.mContext = context;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layout;
        switch (viewType) {
            case 1:
                layout = R.layout.item_album_search;
                break;
            case 2:
                layout = R.layout.item_artist;
                break;
            case 10:
                layout = R.layout.search_section_header;
                break;
            case 0:
            default:
                layout = R.layout.item_song;
                break;
        }
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(layout, null));
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        switch (getItemViewType(i)) {
            case 0:
                itemHolder.binding.setVariable(BR.song, searchResults.get(i));
                break;
            case 1:
                itemHolder.binding.setVariable(BR.album, searchResults.get(i));
                break;
            case 2:
                Artist artist = (Artist) searchResults.get(i);
                itemHolder.binding.setVariable(BR.artist, artist);
                LastFmClient.getInstance(mContext).getArtistInfo(new ArtistQuery(artist.name), new ArtistInfoListener() {
                    @Override
                    public void artistInfoSucess(LastfmArtist artist) {
                        if (artist.mArtwork != null) {
                            itemHolder.binding.setVariable(BR.artistImage, artist.mArtwork.get(0).mUrl);
                        }
                    }

                    @Override
                    public void artistInfoFailed() {

                    }
                });
                break;
            case 10:
                itemHolder.binding.setVariable(BR.header, (String) searchResults.get(i));
            case 3:
                break;
        }
    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ViewDataBinding binding;

        public ItemHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType()) {
                case 0:
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long[] ret = new long[1];
                            ret[0] = ((Song) searchResults.get(getAdapterPosition())).id;
                            MusicPlayer.playAll(mContext, ret, 0, -1, TimberUtils.IdType.NA, false);
                        }
                    }, 100);

                    break;
                case 1:
                    ArrayList<Pair> tranitionViews0 = new ArrayList<>();
                    tranitionViews0.add(0, Pair.create(itemView.findViewById(R.id.album_art), "transition_album_art"));
                    NavigationUtils.navigateToAlbum(mContext, ((Album) searchResults.get(getAdapterPosition())).id, tranitionViews0);
                    break;
                case 2:
                    ArrayList<Pair> tranitionViews1 = new ArrayList<>();
                    tranitionViews1.add(0, Pair.create(itemView.findViewById(R.id.artistImage), "transition_artist_image"));
                    NavigationUtils.navigateToArtist(mContext, ((Artist) searchResults.get(getAdapterPosition())).id, tranitionViews1);
                    break;
                case 3:
                    break;
                case 10:
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Song)
            return 0;
        if (searchResults.get(position) instanceof Album)
            return 1;
        if (searchResults.get(position) instanceof Artist)
            return 2;
        if (searchResults.get(position) instanceof String)
            return 10;
        return 3;
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }
}





