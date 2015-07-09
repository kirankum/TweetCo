package com.tweetco.activities.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.activities.QuickReturnListView;
import com.tweetco.utility.UiUtility;

/**
 * Created by kirankum on 7/9/2015.
 */
public class TweetFragment extends Fragment {

    private ImageFetcher mProfileImageFetcher;
    private ImageFetcher mTweetContentImageFetcher;

    ImageView profilePicImage;
    TextView handle;
    TextView userName;
    TextView tweetContent;
    TextView tweetTime;
    ImageView tweetContentImage;
    ImageView upvoteView;
    ImageView bookmarkView;
    TextView inReplyTo;
    ImageView replyToTweetButton;
    TextView upvotesCount;
    TextView bookmarksCount;

    private int mIterator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mProfileImageFetcher = Utils.getImageFetcher(getActivity(), 60, 60);

        mTweetContentImageFetcher = Utils.getImageFetcher(getActivity(), 60, 60);

        mIterator = getArguments().getInt("iterator");
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.tweet, container, false);

        return v;
    }

}
