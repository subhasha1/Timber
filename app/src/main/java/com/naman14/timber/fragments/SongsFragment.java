package com.naman14.timber.fragments;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.databinding.FragmentMainBinding;
import com.naman14.timber.databinding.FragmentRecyclerviewBinding;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

/**
 * Created by naman on 12/06/15.
 */
public class SongsFragment extends Fragment implements MusicStateListener {

    private SongsListAdapter mAdapter;
    private FragmentRecyclerviewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recyclerview, container, false);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.fastscroller.setRecyclerView(binding.recyclerview);
        new LoadSongs().execute();
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
        return binding.getRoot();
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private class LoadSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mAdapter = new SongsListAdapter(getActivity(), SongLoader.getAllSongs(getActivity()), false);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (getActivity() != null) {
                binding.recyclerview.setAdapter(mAdapter);
                binding.recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            }
        }
    }

}
