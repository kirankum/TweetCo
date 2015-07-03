package com.tweetco.activities.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.LinearLayout;

import com.imagedisplay.util.AsyncTask;
import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.Exceptions.LeaderboardUserNotFoundException;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.activities.Constants;
import com.tweetco.activities.QuickReturnListView;
import com.tweetco.activities.adapter.TweetAdapter;
import com.tweetco.activities.TweetDetailActivity;
import com.tweetco.activities.UserProfileActivity;
import com.tweetco.activities.helper.Helper;
import com.tweetco.dao.Tweet;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.models.tweets.TweetsBaseModel;
import com.tweetco.utility.UiUtility;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 27/06/15.
 */
public class TweetListFragmentBase extends Fragment {

    public interface ITweetListFragmentCallback {
        public void onScroll();
        public void onRefresh();
    }

    private static final String TAG = "TweetListFragment";

    protected TweetAdapter mAdapter;
    protected TweetsBaseModel baseModel;

    private ImageFetcher mProfileImageFetcher;
    private ImageFetcher mTweetContentImageFetcher;
    private QuickReturnListView mListView;
    protected LinearLayout linlaHeaderProgress;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ITweetListFragmentCallback mCallback;

    private List<Integer> tweetIteratorList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mProfileImageFetcher = Utils.getImageFetcher(getActivity(), 60, 60);

        mTweetContentImageFetcher = Utils.getImageFetcher(getActivity(), 60, 60);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.tweetlist, container, false);

        mListView = (QuickReturnListView) v.findViewById(R.id.listView);
        linlaHeaderProgress = (LinearLayout) v.findViewById(R.id.linlaHeaderProgress);

        mSwipeRefreshLayout = UiUtility.getView(v, R.id.tweetList_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                if(mCallback != null) {
                    mCallback.onRefresh();
                }
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mProfileImageFetcher.setPauseWork(true);
                    }
                } else {
                    mProfileImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mCallback != null && mAdapter != null) {
                    // In scroll-to-bottom-to-load mode, when the sum of first visible position and visible count equals the total number
                    // of items in the adapter it reaches the bottom
                    int bufferItemsToShow = mAdapter.getCount() -(firstVisibleItem + visibleItemCount);
                    Log.d(TAG, "There are getCount()="+ mAdapter.getCount()+" firstVisibleItem="+firstVisibleItem+ " visibleItemCount="+visibleItemCount);
                    if(bufferItemsToShow < 10  && mAdapter.canScroll())
                    {
                        mCallback.onScroll();
                    }

                }

            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        Log.v(TAG, "onGlobalLayout layout Done");
                        int visibleChildCount = (mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition()) + 1;
                    }
                });

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume");
        mProfileImageFetcher.setExitTasksEarly(false);
        mTweetContentImageFetcher.setExitTasksEarly(false);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause");
        mProfileImageFetcher.setPauseWork(false);
        mProfileImageFetcher.setExitTasksEarly(true);
        mProfileImageFetcher.flushCache();
        mTweetContentImageFetcher.setPauseWork(false);
        mTweetContentImageFetcher.setExitTasksEarly(true);
        mTweetContentImageFetcher.flushCache();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy ");
        mProfileImageFetcher.closeCache();
        mTweetContentImageFetcher.closeCache();
    }

    protected void initAdapter(Activity activity, List<Integer> list) {
        tweetIteratorList = list;
        mAdapter = new TweetAdapter(activity, R.layout.tweet, list, mProfileImageFetcher, mTweetContentImageFetcher, new TweetAdapter.OnProfilePicClick() {

            @Override
            public void onItemClick(int position) {
                //Show user profile view
                try {
                    int iterator = tweetIteratorList.get(position);
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
        }, new TweetAdapter.OnTweetClick() {

            @Override
            public void onItemClick(int position)
            {
                try {
                    int iterator = tweetIteratorList.get(position);
                    Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);
                    Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                    intent.putExtra("Tweet", tweet);
                    getActivity().startActivity(intent);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, new TweetAdapter.OnReplyClick() {

            @Override
            public void onItemClick(int position)
            {
                try {
                    int iterator = tweetIteratorList.get(position);
                    Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);
                    Helper.launchPostTweetActivity(TweetListFragmentBase.this.getActivity(), "@" + tweet.tweetowner + " ", tweet.iterator, tweet.tweetowner);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, new TweetAdapter.OnUpvoteClick() {
            @Override
            public void onItemClick(final int iterator, boolean selected)
            {

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                baseModel.upvoteTweet(iterator);
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
        }, new TweetAdapter.OnBookmarkClick() {
            @Override
            public void onItemClick(final int iterator, boolean selected)
            {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                baseModel.bookmarkTweet(iterator);

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
        }, new TweetAdapter.OnHideClick() {
            @Override
            public void onItemClick(final int iterator, boolean selected)
            {

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                baseModel.hideTweet(iterator);

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

        mListView.setAdapter(mAdapter);
    }


}
