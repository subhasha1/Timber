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

package com.naman14.timber.subfragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.databinding.FragmentPlaylistPagerBinding;
import com.naman14.timber.dataloaders.LastAddedLoader;
import com.naman14.timber.dataloaders.PlaylistLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.models.Playlist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaylistPagerFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";
    private int pageNumber, songCountInt;
    private int foregroundColor;
    private long firstAlbumID = -1;

    private Playlist playlist;
    FragmentPlaylistPagerBinding binding;

    int[] foregroundColors = {R.color.pink_transparent, R.color.green_transparent, R.color.blue_transparent, R.color.red_transparent, R.color.purple_transparent};

    public static PlaylistPagerFragment newInstance(int pageNumber) {
        PlaylistPagerFragment fragment = new PlaylistPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_pager, container, false);

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity());

        pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        playlist = playlists.get(pageNumber);

        binding.playlistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Pair> tranitionViews = new ArrayList<>();
                tranitionViews.add(0, Pair.create((View) binding.name, "transition_playlist_name"));
                tranitionViews.add(1, Pair.create((View) binding.playlistImage, "transition_album_art"));
                tranitionViews.add(2, Pair.create((View) binding.foreground, "transition_foreground"));
                NavigationUtils.navigateToPlaylistDetail(getActivity(), getPlaylistType(), firstAlbumID, String.valueOf(binding.name.getText()), foregroundColor, playlist.id, tranitionViews);
            }
        });

        setUpPlaylistDetails();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedinstancestate) {
        new loadPlaylistImage().execute("");
    }

    private void setUpPlaylistDetails() {
        binding.name.setText(playlist.name);

        int number = getArguments().getInt(ARG_PAGE_NUMBER) + 1;
        String playlistnumberstring;

        if (number > 9) {
            playlistnumberstring = String.valueOf(number);
        } else {
            playlistnumberstring = "0" + String.valueOf(number);
        }
        binding.number.setText(playlistnumberstring);

        Random random = new Random();
        int rndInt = random.nextInt(foregroundColors.length);

        foregroundColor = foregroundColors[rndInt];
        binding.foreground.setBackgroundColor(foregroundColor);

        if (pageNumber > 2) {
            binding.playlisttype.setVisibility(View.GONE);
        }

    }

    private String getPlaylistType() {
        switch (pageNumber) {
            case 0:
                return Constants.NAVIGATE_PLAYLIST_LASTADDED;
            case 1:
                return Constants.NAVIGATE_PLAYLIST_RECENT;
            case 2:
                return Constants.NAVIGATE_PLAYLIST_TOPTRACKS;
            default:
                return Constants.NAVIGATE_PLAYLIST_USERCREATED;
        }
    }


    private class loadPlaylistImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null) {
                switch (pageNumber) {
                    case 0:
                        List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(getActivity());
                        songCountInt = lastAddedSongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = lastAddedSongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.RecentSongs);
                        List<Song> recentsongs = SongLoader.getSongsForCursor(recentloader.getCursor());
                        songCountInt = recentsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = recentsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.TopTracks);
                        List<Song> topsongs = SongLoader.getSongsForCursor(topTracksLoader.getCursor());
                        songCountInt = topsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = topsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    default:
                        List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlist.id);
                        songCountInt = playlistsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = playlistsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";

                }
            } else return "context is null";

        }

        @Override
        protected void onPostExecute(String uri) {
            ImageLoader.getInstance().displayImage(uri, binding.playlistImage,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    });
            binding.songcount.setText(" " + String.valueOf(songCountInt) + " Songs");
        }

        @Override
        protected void onPreExecute() {
        }
    }


}

