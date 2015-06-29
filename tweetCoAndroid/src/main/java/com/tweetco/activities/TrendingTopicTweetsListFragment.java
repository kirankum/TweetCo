package com.tweetco.activities;

import android.os.Bundle;
import android.view.View;

import com.imagedisplay.util.AsyncTask;
import com.tweetco.datastore.TrendingListSingleton;
import com.tweetco.datastore.TrendingTopicAsKeyTweetsListSingleton;
import com.tweetco.datastore.UserAsKeyTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.TrendingTweetsModel;
import com.tweetco.models.tweets.UserTweetsModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 29/06/15.
 */
public class TrendingTopicTweetsListFragment extends TweetListFragmentBase implements OnChangeListener<List<Integer>> {
    private TrendingTweetsModel model;
    private String mTopic;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new TrendingTweetsModel();
        baseModel = model;
        mTopic = getArguments().getString("topic");
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
                            model.refreshTweetsOfTopicFromServer(mTopic);
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
        TrendingTopicAsKeyTweetsListSingleton.INSTANCE.addListener(mTopic, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UserAsKeyTweetsListSingleton.INSTANCE.removeListener(mTopic, this);
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
                    if( TrendingTopicAsKeyTweetsListSingleton.INSTANCE.getTweetsListForTopic(mTopic) == null ||
                            TrendingTopicAsKeyTweetsListSingleton.INSTANCE.getTweetsListForTopic(mTopic).isEmpty()) {
                        model.refreshTweetsOfTopicFromServer(mTopic);
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
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter == null) {
                    initAdapter(TrendingTopicAsKeyTweetsListSingleton.INSTANCE.getTweetsListForTopic(mTopic));
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
