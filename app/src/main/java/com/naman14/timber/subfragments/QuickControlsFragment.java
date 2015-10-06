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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.databinding.BottomNowplayingCardBinding;
import com.naman14.timber.databinding.FragmentPlaybackControlsBinding;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.nowplaying.BaseNowplayingFragment;
import com.naman14.timber.utils.ImageUtils;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.PlayPauseButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class QuickControlsFragment extends BaseNowplayingFragment implements MusicStateListener {


    private String mArtUrl;
    public static RelativeLayout topContainer;
    private FragmentPlaybackControlsBinding binding;
    private BottomNowplayingCardBinding nowplayingCardBinding;

    private boolean duetoplaypause = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playback_controls, container, false);
        nowplayingCardBinding = DataBindingUtil.bind(binding.getRoot().findViewById(R.id.bottom_nowplaying));
        topContainer = nowplayingCardBinding.topContainer;
        ProgressBar mProgress = nowplayingCardBinding.songProgressNormal;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mProgress.getLayoutParams();
        mProgress.measure(0, 0);
        layoutParams.setMargins(0, -(mProgress.getMeasuredHeight() / 2), 0, 0);
        mProgress.setLayoutParams(layoutParams);
        mProgress.setScaleY(0.5f);

        if (isThemeIsLight()) {
            nowplayingCardBinding.playPause.setColor(getActivity().getResources().getColor(R.color.colorAccent));
        } else if (isThemeIsDark()) {
            nowplayingCardBinding.playPause.setColor(getActivity().getResources().getColor(R.color.colorAccentDarkTheme));
        } else
            nowplayingCardBinding.playPause.setColor(getActivity().getResources().getColor(R.color.colorAccentBlack));

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);


        return binding.getRoot();
    }

    public void updateControlsFragment() {
        //let basenowplayingfragment take care of this
        setSongDetails(binding.getRoot());

    }

    //to update the permanent now playing card at the bottom
    public void updateNowplayingCard() {
        binding.songTitle.setText(MusicPlayer.getTrackName());
        binding.songArtist.setText(MusicPlayer.getArtistName());
        if (!duetoplaypause) {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), nowplayingCardBinding.albumArtNowplayingcard,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
                            if (getActivity() != null)
                                new setBlurredAlbumArt().execute(failedBitmap);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (getActivity() != null)
                                new setBlurredAlbumArt().execute(loadedImage);

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        }
        duetoplaypause = false;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        topContainer = nowplayingCardBinding.topContainer;
    }


    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!nowplayingCardBinding.playPause.isPlayed()) {
                nowplayingCardBinding.playPause.setPlayed(true);
                nowplayingCardBinding.playPause.startAnimation();
            } else {
                nowplayingCardBinding.playPause.setPlayed(false);
                nowplayingCardBinding.playPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                }
            }, 200);

        }
    };

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            if (!nowplayingCardBinding.playPause.isPlayed()) {
                nowplayingCardBinding.playPause.setPlayed(true);
                nowplayingCardBinding.playPause.startAnimation();
            }
        } else {
            if (nowplayingCardBinding.playPause.isPlayed()) {
                nowplayingCardBinding.playPause.setPlayed(false);
                nowplayingCardBinding.playPause.startAnimation();
            }
        }
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        //only update nowplayingcard,quick controls will be updated by basenowplayingfragment's onMetaChanged
        updateNowplayingCard();
        updateState();
        //TODO
        updateControlsFragment();
    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 6);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                if (binding.blurredAlbumart.getDrawable() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{
                                    binding.blurredAlbumart.getDrawable(),
                                    result
                            });
                    binding.blurredAlbumart.setImageDrawable(td);
                    td.startTransition(400);

                } else {
                    binding.blurredAlbumart.setImageDrawable(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }


}
