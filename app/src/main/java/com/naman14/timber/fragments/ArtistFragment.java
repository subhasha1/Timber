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

package com.naman14.timber.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.ArtistAdapter;
import com.naman14.timber.databinding.FragmentRecyclerviewBinding;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

public class ArtistFragment extends Fragment {

    ArtistAdapter mAdapter;
    private boolean isGrid;
    FragmentRecyclerviewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recyclerview, container, false);

        isGrid = PreferencesUtility.getInstance(getActivity()).isArtistsInGrid();

        if (isGrid) {
            binding.recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            binding.fastscroller.setVisibility(View.GONE);
        } else {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.fastscroller.setRecyclerView(binding.recyclerview);
        }

        new loadArtists().execute("");
        return binding.getRoot();
    }


    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mAdapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (getActivity() != null) {
                binding.recyclerview.setAdapter(mAdapter);
                if (isGrid) {
                    int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
                    binding.recyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                } else
                    binding.recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {


            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;

        }
    }


}
