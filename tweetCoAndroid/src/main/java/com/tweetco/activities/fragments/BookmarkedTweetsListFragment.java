package com.tweetco.activities.fragments;

import android.os.Bundle;
import android.view.View;

import com.imagedisplay.util.AsyncTask;
import com.tweetco.datastore.BookmarkedTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.BookmarkedTweetsModel;

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
        baseModel = model;
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
        if(isAdded()) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter == null) {
                        initAdapter(BookmarkedTweetsListFragment.this.getActivity(), BookmarkedTweetsListSingleton.INSTANCE.getBookmarkedTweets());
                    }

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


}
