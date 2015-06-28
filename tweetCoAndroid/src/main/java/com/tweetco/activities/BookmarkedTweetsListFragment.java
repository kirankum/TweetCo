package com.tweetco.activities;

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
import com.tweetco.datastore.BookmarkedTweetsListSingleton;
import com.tweetco.datastore.HomeFeedTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.BookmarkedTweetsModel;
import com.tweetco.models.tweets.HomeFeedTweetsModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 28/06/15.
 */
public class BookmarkedTweetsListFragment extends TweetListFragmentBase implements OnChangeListener<List<Integer>> {

    private BookmarkedTweetsModel model;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        model = new BookmarkedTweetsModel();
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
                            model.refreshBookmarkedTweetsListFromServer();
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
        BookmarkedTweetsListSingleton.INSTANCE.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BookmarkedTweetsListSingleton.INSTANCE.removeListener(this);
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
                    if(BookmarkedTweetsListSingleton.INSTANCE.getBookmarkedTweets().isEmpty()) {
                        model.refreshBookmarkedTweetsListFromServer();
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
                    initAdapter(BookmarkedTweetsListSingleton.INSTANCE.getBookmarkedTweets());
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }


}
