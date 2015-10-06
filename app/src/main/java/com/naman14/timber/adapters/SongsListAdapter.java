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
import android.databinding.BindingAdapter;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.databinding.ItemSongBinding;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.BubbleTextGetter;

import java.util.List;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.ItemHolder> implements BubbleTextGetter {

    private List<Song> arraylist;
    private Activity mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    public int currentlyPlayingPosition;

    private int lastPosition = -1;

    public SongsListAdapter(Activity context, List<Song> arraylist, boolean isPlaylistSong) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(ItemSongBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false), songIDs);
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        Song localItem = arraylist.get(i);
        itemHolder.bind(localItem);
        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            currentlyPlayingPosition = i;
        }
        if (isPlaylist && PreferencesUtility.getInstance(mContext).getAnimations()) {
            if (TimberUtils.isLollipop())
                setAnimation(itemHolder.itemView, i);
            else {
                if (i > 10)
                    setAnimation(itemHolder.itemView, i);
            }
        }

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemSongBinding binding;
        private long[] songIds;

        public ItemHolder(ItemSongBinding binding, long[] songIds) {
            super(binding.getRoot());
            this.binding = binding;
            this.songIds = songIds;
            itemView.setOnClickListener(this);
            binding.setPlayList(isPlaylist);
        }

        public void bind(Song song) {
            binding.setSong(song);
        }

        @Override
        public void onClick(final View v) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(v.getContext(), songIds, getAdapterPosition(), -1, TimberUtils.IdType.NA, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(currentlyPlayingPosition);
                            notifyItemChanged(getAdapterPosition());
                        }
                    }, 50);
                }
            }, 100);

        }

    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    @BindingAdapter("bind:itemSongType")
    public static void setItemViewStyles(TextView view, boolean isPlaylist) {
        if (isPlaylist) {
            view.setTextColor(ContextCompat.getColor(view.getContext(), android.R.color.white));
        }
    }

    @Override
    public String getTextToShowInBubble(final int pos) {
        Character ch = arraylist.get(pos).title.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}


