package com.tweetco.activities.fragments;

import android.os.Bundle;
import android.view.View;

import com.imagedisplay.util.AsyncTask;
import com.tweetco.datastore.TodayTweetsListSingleton;
import com.tweetco.datastore.TrendingTopicAsKeyTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.TrendingTweetsModel;
import com.tweetco.models.tweets.TweetsTodayModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 05/07/15.
 */
public class TodayTweetsListFragment extends TweetListFragmentBase implements OnChangeListener<List<Integer>> {
    private TweetsTodayModel model;
    private String mTopic;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new TweetsTodayModel();
        mCallback = new ITweetListFragmentCallback() {
            @Override
            public void onScroll() {

            }

            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.refreshTodayTweetsListFromServer();
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
    public void onResume()
    {
        super.onResume();
        TodayTweetsListSingleton.INSTANCE.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        TodayTweetsListSingleton.INSTANCE.removeListener(this);
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
                    if( TodayTweetsListSingleton.INSTANCE.getTodayTweets().isEmpty()) {
                        model.refreshTodayTweetsListFromServer();
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
                        initAdapter(TodayTweetsListFragment.this.getActivity(), TodayTweetsListSingleton.INSTANCE.getTodayTweets());
                    }

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
