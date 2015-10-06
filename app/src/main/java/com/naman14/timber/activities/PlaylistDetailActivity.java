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

package com.naman14.timber.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.databinding.ActivityPlaylistDetailBinding;
import com.naman14.timber.dataloaders.LastAddedLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.listeners.SimplelTransitionListener;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private Activity mContext = PlaylistDetailActivity.this;
    String action;
    long playlistID;

    private SongsListAdapter mAdapter;
    private ActivityPlaylistDetailBinding binding;


    @TargetApi(21)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_detail);

        action = getIntent().getAction();

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        setAlbumart();

        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(this).getAnimations()) {
            getWindow().getEnterTransition().addListener(new EnterTransitionListener());
        } else {
            setUpSongs(action);
        }

    }

    private void setAlbumart() {
        binding.name.setText(getIntent().getExtras().getString(Constants.PLAYLIST_NAME));
        binding.foreground.setBackgroundColor(getIntent().getExtras().getInt(Constants.PLAYLIST_FOREGROUND_COLOR));
        binding.setAlbumId(getIntent().getExtras().getLong(Constants.ALBUM_ID));
    }

    private void setUpSongs(String action) {
        if (!TextUtils.isEmpty(action)) {
            new LoadSongs().execute(action);
        } else {
            Log.d("PlaylistDetail", "mo action specified");
        }
    }

    private class LoadSongs extends AsyncTask<String, Void, Void> {
        /**
         * Here adapter to binding has been set on background thread which demonstrates that data on view can be set even
         * in background thread using this library.
         *
         * @param params Action
         * @return
         */
        @Override
        protected Void doInBackground(String... params) {
            String action = params[0];
            List<Song> songs = null;
            if (action.equals(Constants.NAVIGATE_PLAYLIST_LASTADDED)) {
                songs = LastAddedLoader.getLastAddedSongs(mContext);
            } else if (action.equals(Constants.NAVIGATE_PLAYLIST_RECENT)) {
                TopTracksLoader loader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
                songs = SongLoader.getSongsForCursor(loader.getCursor());
            } else if (action.equals(Constants.NAVIGATE_PLAYLIST_TOPTRACKS)) {
                TopTracksLoader loader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
                songs = SongLoader.getSongsForCursor(loader.getCursor());
            } else if (action.equals(Constants.NAVIGATE_PLAYLIST_USERCREATED)) {
                songs = PlaylistSongLoader.getSongsInPlaylist(mContext, playlistID);
            }
            mAdapter = new SongsListAdapter(mContext, songs, true);
            if (!isDestroyed()) {
                binding.setAdapter(mAdapter);
                if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(mContext).getAnimations()) {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.setItemDecor(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
                        }
                    }, 250);
                } else
                    binding.setItemDecor(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
            }
            return null;
        }

    }


    private class EnterTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
            setUpSongs(action);
        }

        public void onTransitionStart(Transition paramTransition) {
        }

    }


}
