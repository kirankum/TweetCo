package com.tweetco.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.floatingactionbuttonexample.FloatingActionButton;
import com.imagedisplay.util.AsyncTask;
import com.onefortybytes.R;
import com.tweetco.activities.Constants;
import com.tweetco.activities.helper.Helper;
import com.tweetco.datastore.HomeFeedTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.HomeFeedTweetsModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 27/06/15.
 */
public class HomeFeedTweetsListFragment extends TweetListFragmentBase implements OnChangeListener<List<Integer>> {

    private HomeFeedTweetsModel model;
    private FloatingActionButton mFloatingActionButton;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        model = new HomeFeedTweetsModel();
        baseModel = model;
        mCallback = new ITweetListFragmentCallback() {
            @Override
            public void onScroll() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.refreshOlderTweetsFromServer();
                        } catch (MalformedURLException e) {

                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }.execute();
            }

            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.refreshLatestTweetsFromServer();
                        } catch (MalformedURLException e) {

                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }.execute();
            }
        };
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup activityViewRoot = ((ViewGroup)v.findViewById(R.id.listView));

        mFloatingActionButton = (com.example.floatingactionbuttonexample.FloatingActionButton) v.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Helper.launchPostTweetActivity(HomeFeedTweetsListFragment.this.getActivity(), "", -1, null);

            }
        });

        mFloatingActionButton.attachToListView((ListView) activityViewRoot);
        mFloatingActionButton.setVisibility(View.VISIBLE);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        HomeFeedTweetsListSingleton.INSTANCE.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        HomeFeedTweetsListSingleton.INSTANCE.removeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(HomeFeedTweetsListSingleton.INSTANCE.getHomeFeedTweets().isEmpty()) {
                        model.refreshLatestTweetsFromServer();
                    }
                    else {
                        refreshUiOnUIThread();
                    }

                }
                catch (MalformedURLException e)
                {

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                // HIDE THE SPINNER AFTER LOADING FEEDS
                linlaHeaderProgress.setVisibility(View.GONE);
            }

        }.execute();
    }

    @Override
    public void onChange(List<Integer> model) {
        refreshUiOnUIThread();
    }

    private void refreshUiOnUIThread() {
        if(isAdded()) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter == null) {
                        initAdapter(HomeFeedTweetsListFragment.this.getActivity(), HomeFeedTweetsListSingleton.INSTANCE.getHomeFeedTweets());
                    }

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
