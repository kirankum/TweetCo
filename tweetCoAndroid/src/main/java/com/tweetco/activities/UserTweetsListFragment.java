package com.tweetco.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.imagedisplay.util.AsyncTask;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.UserAsKeyTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.BookmarkedTweetsModel;
import com.tweetco.models.tweets.UserTweetsModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 28/06/15.
 */
public class UserTweetsListFragment extends TweetListFragmentBase implements OnChangeListener<List<Integer>> {
    private UserTweetsModel model;
    private String mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new UserTweetsModel();
        mUsername = getArguments().getString("username");
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
                            model.refreshTweetsFromServer(mUsername);
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
        UserAsKeyTweetsListSingleton.INSTANCE.addListener(mUsername, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UserAsKeyTweetsListSingleton.INSTANCE.removeListener(mUsername, this);
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
                    if( UserAsKeyTweetsListSingleton.INSTANCE.getTweetsListForUser(mUsername) == null ||
                            UserAsKeyTweetsListSingleton.INSTANCE.getTweetsListForUser(mUsername).isEmpty()) {
                        model.refreshTweetsFromServer(mUsername);
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
                    initAdapter(UserAsKeyTweetsListSingleton.INSTANCE.getTweetsListForUser(mUsername));
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
