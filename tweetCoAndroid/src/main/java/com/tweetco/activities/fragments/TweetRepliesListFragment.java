package com.tweetco.activities.fragments;

import android.os.Bundle;
import android.view.View;

import com.imagedisplay.util.AsyncTask;
import com.tweetco.datastore.IteratorAsKeyReplyTweetsListSingleton;
import com.tweetco.datastore.UserAsKeyTweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.TweetRepliesModel;
import com.tweetco.models.tweets.UserTweetsModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 09/07/15.
 */
public class TweetRepliesListFragment extends TweetListFragmentBase implements OnChangeListener<List<Integer>> {
    private TweetRepliesModel model;
    private String mIterator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new TweetRepliesModel();
        baseModel = model;
        mIterator = getArguments().getString("iterator");
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
                            model.refreshReplyTweetsOfTweet(mIterator);
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
        IteratorAsKeyReplyTweetsListSingleton.INSTANCE.addListener(mIterator, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        IteratorAsKeyReplyTweetsListSingleton.INSTANCE.removeListener(mIterator, this);
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
                    if( IteratorAsKeyReplyTweetsListSingleton.INSTANCE.getRepliesForIterator(mIterator) == null ||
                            IteratorAsKeyReplyTweetsListSingleton.INSTANCE.getRepliesForIterator(mIterator).isEmpty()) {
                        model.refreshReplyTweetsOfTweet(mIterator);
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
                        initAdapter(TweetRepliesListFragment.this.getActivity(), IteratorAsKeyReplyTweetsListSingleton.INSTANCE.getRepliesForIterator(mIterator));
                    }

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
