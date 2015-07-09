package com.tweetco.activities.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imagedisplay.util.AsyncTask;
import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.Exceptions.LeaderboardUserNotFoundException;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.activities.Constants;
import com.tweetco.activities.QuickReturnListView;
import com.tweetco.activities.TweetDetailActivity;
import com.tweetco.activities.UserProfileActivity;
import com.tweetco.activities.helper.Helper;
import com.tweetco.activities.helper.TweetUIHolder;
import com.tweetco.dao.Tweet;
import com.tweetco.datastore.BookmarkedTweetsListSingleton;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.tweets.TweetModel;
import com.tweetco.models.tweets.TweetsBaseModel;
import com.tweetco.utility.UiUtility;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankum on 7/9/2015.
 */
public class TweetFragment extends Fragment implements OnChangeListener<Tweet> {

    private ImageFetcher mProfileImageFetcher;
    private ImageFetcher mTweetContentImageFetcher;

    private LinearLayout linlaHeaderProgress;

    private TweetModel model;
    private int mIterator;
    private TweetUIHolder mTweetViewHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        model = new TweetModel();

        mProfileImageFetcher = Utils.getImageFetcher(getActivity(), 60, 60);

        mTweetContentImageFetcher = Utils.getImageFetcher(getActivity(), 60, 60);

        mIterator = getArguments().getInt("iterator");
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.tweet, container, false);

        linlaHeaderProgress = (LinearLayout) v.findViewById(R.id.linlaHeaderProgress);

        mTweetViewHolder = new TweetUIHolder(v, mProfileImageFetcher, mTweetContentImageFetcher, new TweetUIHolder.OnTweetItemClick() {

            @Override
            public void onClick(View v, int iterator) {
                //Show user profile view
                try {
                    Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);
                    String owner = tweet.tweetowner;
                    if(!TextUtils.isEmpty(owner))
                    {
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra(Constants.USERNAME_STR, owner);
                        getActivity().startActivity(intent);
                    }
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }, new TweetUIHolder.OnTweetItemClick() {

            @Override
            public void onClick(View v, int iterator) {
                try {
                    Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);
                    Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                    intent.putExtra("Tweet", tweet);
                    getActivity().startActivity(intent);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, new TweetUIHolder.OnTweetItemClick() {

            @Override
            public void onClick(View v, int iterator) {
                try {
                    Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);
                    Helper.launchPostTweetActivity(TweetFragment.this.getActivity(), "@" + tweet.tweetowner + " ", tweet.iterator, tweet.tweetowner);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, new TweetUIHolder.OnTweetItemClick() {

            @Override
            public void onClick(View v, final int iterator) {

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.upvoteTweet(iterator);
                        } catch (TweetNotFoundException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (LeaderboardUserNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();

            }
        }, new TweetUIHolder.OnTweetItemClick() {

            @Override
            public void onClick(View v, final int iterator) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.bookmarkTweet(iterator);

                        } catch (TweetNotFoundException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (LeaderboardUserNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();


            }
        }, new TweetUIHolder.OnTweetItemClick() {

            @Override
            public void onClick(View v, final int iterator) {

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.hideTweet(iterator);

                        } catch (TweetNotFoundException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (LeaderboardUserNotFoundException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();


            }
        });

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        TweetsListSingleton.INSTANCE.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        TweetsListSingleton.INSTANCE.removeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            TweetsListSingleton.INSTANCE.getTweet(mIterator);
            refreshUiOnUIThread();
        } catch (TweetNotFoundException e) {
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
                        model.refreshTweetFromServer(String.valueOf(mIterator));
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
    }

    @Override
    public void onChange(Tweet model) {
        refreshUiOnUIThread();
    }

    private void refreshUiOnUIThread() {
        if(isAdded()) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTweetViewHolder.loadTweetWithIterator(mIterator);
                }
            });
        }
    }

}
