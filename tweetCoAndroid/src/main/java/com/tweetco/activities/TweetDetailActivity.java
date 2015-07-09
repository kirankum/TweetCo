package com.tweetco.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.Exceptions.LeaderboardUserNotFoundException;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.Exceptions.TweetUserNotFoundException;
import com.tweetco.TweetCo;
import com.tweetco.activities.fragments.TweetFragment;
import com.tweetco.activities.fragments.TweetRepliesListFragment;
import com.tweetco.activities.helper.Helper;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.UsersListSigleton;
import com.tweetco.models.tweets.TweetsBaseModel;
import com.tweetco.tweets.TweetCommonData;
import com.tweetco.utility.UiUtility;

import java.net.MalformedURLException;

public class TweetDetailActivity extends TweetCoBaseActivity 
{
	public static final String TAG = "TweetAdapter";

	//The first imageFetcher loads profileImages and the second one loads the tweetcontent images.
	ImageFetcher mProfileImageFetcher;
	ImageFetcher mTweetContentImageFetcher;

	private FrameLayout mMainTweetContainer;
	private FrameLayout mMainTweetDetailsContainer;
	private TextView tweetUpvotesCount;
	private TextView tweetBookmarksCount;
	private LinearLayout repliesText;
	private LinearLayout inReplyToText;
	
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		
		setContentView(R.layout.tweetdetail);
		
		final Tweet tweet = (Tweet)getIntent().getParcelableExtra("Tweet");
		
		mProfileImageFetcher = Utils.getImageFetcher(this, 60, 60);

		mTweetContentImageFetcher = Utils.getImageFetcher(this, 60, 60);

		mMainTweetContainer = UiUtility.getView(this, R.id.mainTweetFragmentContainer);
		mMainTweetDetailsContainer = UiUtility.getView(this, R.id.tweetsDetailFragmentContainer);
		tweetUpvotesCount = UiUtility.getView(this, R.id.upvotesCount);
		tweetBookmarksCount = UiUtility.getView(this, R.id.bookmarksCount);
		repliesText = UiUtility.getView(this, R.id.repliesText);
		inReplyToText = UiUtility.getView(this, R.id.inReplyToTextContainter);

		tweetBookmarksCount.setText(String.valueOf(getCount(tweet.bookmarkers, ";") + " Bookmarks"));
		tweetBookmarksCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if(!TextUtils.isEmpty(tweet.bookmarkers))
				{
					Intent intent = new Intent(getApplication(), UsersListActivity.class);
					intent.putExtra("title", "Bookmarked by");
					intent.putExtra("usersList", tweet.bookmarkers);
					startActivity(intent);
				}
			}
		});

		tweetUpvotesCount.setText(String.valueOf(getCount(tweet.upvoters, ";") + " Upvotes"));
		tweetUpvotesCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if(!TextUtils.isEmpty(tweet.upvoters))
				{
					Intent intent = new Intent(getApplication(), UsersListActivity.class);
					intent.putExtra("title", "Upvoted by");
					intent.putExtra("usersList", tweet.upvoters);
					startActivity(intent);
				}
			}
		});

		FragmentTransaction ftMainTweet = getSupportFragmentManager().beginTransaction();
		TweetFragment tweetFragment = new TweetFragment();
		Bundle newTweetBundle = new Bundle();
		newTweetBundle.putInt("iterator", tweet.iterator);
		tweetFragment.setArguments(newTweetBundle);
		ftMainTweet.replace(R.id.mainTweetFragmentContainer, tweetFragment);
		ftMainTweet.commit();

		if(!TextUtils.isEmpty(tweet.replies))
		{
			SetViewForReplies(true);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        TweetRepliesListFragment fragment = new TweetRepliesListFragment();
	        Bundle newBundle = new Bundle();
			newBundle.putString("iterator", String.valueOf(tweet.iterator));
			fragment.setArguments(newBundle);
	        ft.replace(R.id.tweetsDetailFragmentContainer, fragment);
	        ft.commit();
		}
		else if(!TextUtils.isEmpty(tweet.inreplyto))
		{
			SetViewForReplies(false);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        TweetFragment fragment = new TweetFragment();
	        Bundle newBundle = new Bundle();
			newBundle.putInt("iterator", Integer.valueOf(tweet.inreplyto));
			fragment.setArguments(newBundle);
	        ft.replace(R.id.tweetsDetailFragmentContainer, fragment);
	        ft.commit();
		}
	}
	
	private void SetViewForReplies(boolean sourceTweet)
	{
		if(sourceTweet)
		{
			repliesText.setVisibility(View.VISIBLE);
			inReplyToText.setVisibility(View.GONE);
		}
		else
		{
			repliesText.setVisibility(View.GONE);
			inReplyToText.setVisibility(View.VISIBLE);
		}
		
		
	}

	private static int getCount(String input, String delimeter)
	{
		int count = 0;

		if(!TextUtils.isEmpty(input))
		{
			count = input.split(delimeter).length;
		}

		return count;
	}
	


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

}
