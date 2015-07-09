package com.tweetco.activities.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.TweetCo;
import com.tweetco.activities.Constants;
import com.tweetco.activities.ImageViewActivity;
import com.tweetco.activities.InfiniteScrollListPageListener;
import com.tweetco.activities.Linkify;
import com.tweetco.activities.TweetUtils;
import com.tweetco.activities.helper.TweetUIHolder;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;
import com.tweetco.tweets.TweetCommonData;

import java.util.List;


/**
 * The main adapter that backs the GridView. This is fairly standard except the number of
 * columns in the GridView is used to create a fake top row of empty views as we use a
 * transparent ActionBar and don't want the real top row of images to start off covered by it.
 */
public class TweetAdapter extends ArrayAdapter<Integer>
{
	public static final String TAG = "TweetAdapter";

	private final Context mContext;
	private ImageFetcher mProfileImageFetcher; //Fetches the images

	private ImageFetcher mTweetContentImageFetcher2; //Fetches the images



	private TweetUIHolder.OnTweetItemClick mOnProfilePicClickCallback;
	private TweetUIHolder.OnTweetItemClick mOnTweetClickCallback;
	private TweetUIHolder.OnTweetItemClick mOnReplyClickCallback;
	private TweetUIHolder.OnTweetItemClick mOnUpvoteClick;
	private TweetUIHolder.OnTweetItemClick mOnBookmarkClick;
	private TweetUIHolder.OnTweetItemClick mOnHideClick;

	protected InfiniteScrollListPageListener mInfiniteListPageListener;

	// A lock to prevent another scrolling event to be triggered if one is already in session
	private boolean canScroll = true;
	// A flag to enable/disable row clicks
	protected boolean rowEnabled = true;
	private long lastDataSetChangedTime = System.currentTimeMillis();

	public void lock()
	{
		canScroll = false;
	}

	public void unlock() 
	{
		canScroll = true;
	}

	public boolean canScroll()
	{
		long currentTime = System.currentTimeMillis();
		if((currentTime- lastDataSetChangedTime) > Constants.THROTTLE_TIME_BETWEEN_DOWN_SCROLL)
		{
			canScroll = true;
		}
		return canScroll;
	}


	public TweetAdapter(Context context, int resource, List<Integer> objects, ImageFetcher imageFetcher,
						ImageFetcher imageFetcher2, TweetUIHolder.OnTweetItemClick onProfilePicClickCallback,
						TweetUIHolder.OnTweetItemClick onTweetClickCallback, TweetUIHolder.OnTweetItemClick onReplyClickCallback,
						TweetUIHolder.OnTweetItemClick onUpvoteClick, TweetUIHolder.OnTweetItemClick onBookmarkClick, TweetUIHolder.OnTweetItemClick onHideClick)
	{
		super(context, resource, objects);
		mContext = context;
		mProfileImageFetcher = imageFetcher;
		mTweetContentImageFetcher2 = imageFetcher2;

		mOnProfilePicClickCallback = onProfilePicClickCallback;
		mOnTweetClickCallback = onTweetClickCallback;
		mOnReplyClickCallback = onReplyClickCallback;
		mOnBookmarkClick = onBookmarkClick;
		mOnHideClick = onHideClick;
		mOnUpvoteClick = onUpvoteClick;
	}

	@Override
	public void notifyDataSetChanged() 
	{
		Log.v(TAG, "notifyDataSetChanged()");
		super.notifyDataSetChanged();
		lastDataSetChangedTime =  System.currentTimeMillis();
	}
	@Override
	public void notifyDataSetInvalidated() 
	{
		Log.v(TAG, "notifyDataSetInvalidated()");
		super.notifyDataSetInvalidated();
		lastDataSetChangedTime =  System.currentTimeMillis();
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup container) 
	{
		Log.v(TAG, "getView called for position ="+position +" convertView="+(convertView!=null));

		if (convertView == null) 
		{ 
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.tweet, container, false);
			TweetUIHolder viewholder = new TweetUIHolder(convertView, mProfileImageFetcher, mTweetContentImageFetcher2,
					mOnProfilePicClickCallback, mOnTweetClickCallback, mOnReplyClickCallback, mOnUpvoteClick, mOnBookmarkClick, mOnHideClick);

			convertView.setTag(viewholder);

		} 

		TweetUIHolder holder = (TweetUIHolder) convertView.getTag();

		int iterator = getItem(position);

		holder.loadTweetWithIterator(iterator);

		return convertView;
	}
	
	//This notifies that the list has ended
	public void notifyEndOfList() 
	{
		// When there is no more to load use the lock to prevent loading from happening
		lock();
		// More actions when there is no more to load
		if (mInfiniteListPageListener != null) {
			mInfiniteListPageListener.endOfList();
		}
	}

	//This notifies that there are more tweets to be loaded
	public void notifyHasMore() 
	{
		// Release the lock when there might be more to load
		unlock();
		// More actions when it might have more to load
		if (mInfiniteListPageListener != null) {
			mInfiniteListPageListener.hasMore();
		}
	}

	public void setInfiniteListPageListener(InfiniteScrollListPageListener infiniteListPageListener) 
	{
		this.mInfiniteListPageListener = infiniteListPageListener;
	}
}
