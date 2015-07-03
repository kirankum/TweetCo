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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.onefortybytes.R;
import com.tweetco.TweetCo;
import com.tweetco.activities.Constants;
import com.tweetco.activities.ImageViewActivity;
import com.tweetco.activities.InfiniteScrollListPageListener;
import com.tweetco.activities.Linkify;
import com.tweetco.activities.TweetUtils;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;
import com.tweetco.tweets.TweetCommonData;
import com.tweetco.utility.UiUtility;

import java.util.List;


/**
 * The main adapter that backs the GridView. This is fairly standard except the number of
 * columns in the GridView is used to create a fake top row of empty views as we use a
 * transparent ActionBar and don't want the real top row of images to start off covered by it.
 */
public class TweetAdapter extends ArrayAdapter<Integer>
{
	public static final String TAG = "TweetAdapter";

	public interface OnProfilePicClick
	{
		void onItemClick(int position);
	}
	
	public interface OnTweetClick
	{
		void onItemClick(int position);
	}
	
	public interface OnReplyClick
	{
		void onItemClick(int position);
	}

	public interface OnUpvoteClick {
		void onItemClick(int iterator, boolean selected);
	}

	public interface OnBookmarkClick {
		void onItemClick(int iterator, boolean selected);
	}

	public interface OnHideClick {
		void onItemClick(int iterator, boolean selected);
	}

	private final Context mContext;
	private ImageFetcher mImageFetcher; //Fetches the images

	private ImageFetcher mImageFetcher2; //Fetches the images



	private OnProfilePicClick mOnProfilePicClickCallback;
	private OnTweetClick mOnTweetClickCallback;
	private OnReplyClick mOnReplyClickCallback;
	private OnUpvoteClick mOnUpvoteClick;
	private OnBookmarkClick mOnBookmarkClick;
	private OnHideClick mOnHideClick;

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



	private static class ViewHolderForBookmarkUpVoteAndHide
	{
		int position;
		int iterator;
		String OwenerName;
	}

	public TweetAdapter(Context context, int resource, List<Integer> objects, ImageFetcher imageFetcher,
						ImageFetcher imageFetcher2, OnProfilePicClick onProfilePicClickCallback,
						OnTweetClick onTweetClickCallback, OnReplyClick onReplyClickCallback,
						OnUpvoteClick onUpvoteClick, OnBookmarkClick onBookmarkClick, OnHideClick onHideClick)
	{
		super(context, resource, objects);
		mContext = context;
		mImageFetcher = imageFetcher;
		mImageFetcher2 = imageFetcher2;

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



	private static class ViewHolder
	{
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
		//		ImageView hideTweet;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup container) 
	{
		Log.v(TAG, "getView called for position ="+position +" convertView="+(convertView!=null));

		if (convertView == null) 
		{ 
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.tweet, container, false);
			ViewHolder viewholder = new ViewHolder();
			viewholder.profilePicImage = (ImageView)convertView.findViewById(R.id.profile_pic);
			viewholder.profilePicImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
			viewholder.handle = (TextView) convertView.findViewById(R.id.handle);
			viewholder.userName = (TextView) convertView.findViewById(R.id.username);
			viewholder.tweetContent = (TextView) convertView.findViewById(R.id.tweetcontent);
			viewholder.tweetTime = (TextView) convertView.findViewById(R.id.time);
			viewholder.inReplyTo = UiUtility.getView(convertView, R.id.in_reply_to);
			viewholder.replyToTweetButton = UiUtility.getView(convertView, R.id.replyToTweet);
			viewholder.upvotesCount = UiUtility.getView(convertView, R.id.tweet_upvoteCount);
			viewholder.bookmarksCount = UiUtility.getView(convertView, R.id.tweet_bookmarksCount);

			viewholder.tweetContentImage = (ImageView) convertView.findViewById(R.id.tweet_content_image);
			viewholder.upvoteView = (ImageView) convertView.findViewById(R.id.upvote);
			viewholder.bookmarkView = (ImageView) convertView.findViewById(R.id.bookmark);
			//			viewholder.hideTweet = (ImageView) convertView.findViewById(R.id.hide);
			convertView.setTag(viewholder);

		} 

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.profilePicImage.setOnClickListener(new OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				mOnProfilePicClickCallback.onItemClick(position);
			}
		});
		
		holder.tweetContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mOnTweetClickCallback.onItemClick(position);
				
			}
		});



		//Load TextFields here
		int iterator = getItem(position);
		try {
			final Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);

			//TODO Check if user is not available
			TweetUser tweeter = (TweetUser) UsersListSigleton.INSTANCE.getUser(tweet.tweetowner.toLowerCase());
			if (tweet != null)
			{
				String username = null;
				String displayName = null;
				if(tweeter == null)
				{
					username = " ";
					displayName = "Anonymous";
				}
				else
				{
					username = Utils.getTweetHandle(tweeter.username);
					displayName = tweeter.displayname;
				}

				holder.handle.setText(username);
				holder.userName.setText(displayName);
				holder.tweetContent.setText(tweet.tweetcontent);
				Linkify.addLinks(holder.tweetContent, Linkify.WEB_URLS | Linkify.HASH_TAGS | Linkify.USER_HANDLE);

				holder.tweetContent.setMovementMethod(new LinkMovementMethod());

				loadTweetImage(tweet, holder.tweetContentImage);

				holder.replyToTweetButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						mOnReplyClickCallback.onItemClick(position);
					}
				});

				holder.tweetTime.setText(Utils.getTime(tweet.__createdAt));

				ViewHolderForBookmarkUpVoteAndHide viewHolderBookMarkUpvoteAndHide = new ViewHolderForBookmarkUpVoteAndHide();
				viewHolderBookMarkUpvoteAndHide.iterator = tweet.iterator;
				viewHolderBookMarkUpvoteAndHide.OwenerName = tweet.tweetowner;
				viewHolderBookMarkUpvoteAndHide.position = position;

				if(!TextUtils.isEmpty(tweet.inreplyto))
				{
					holder.inReplyTo.setVisibility(View.VISIBLE);
					holder.inReplyTo.setText("In reply to " + tweet.sourceuser);
				}
				else
				{
					holder.inReplyTo.setVisibility(View.GONE);
				}

				//UpVote ImageView

				holder.upvoteView.setTag(viewHolderBookMarkUpvoteAndHide);
				setUpVoteFlag(holder.upvoteView, holder.upvotesCount, tweet,TweetCommonData.getUserName());
				holder.upvoteView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View upvoteView)
					{
						upvoteView.startAnimation(AnimationUtils.loadAnimation(TweetCo.mContext, R.anim.animation));
						upvoteView.setSelected(true);
						ViewHolderForBookmarkUpVoteAndHide holder = (ViewHolderForBookmarkUpVoteAndHide) upvoteView.getTag();
						mOnUpvoteClick.onItemClick(holder.iterator, true);
					}

				});


				holder.bookmarkView.setTag(viewHolderBookMarkUpvoteAndHide);
				setBookMarkFlag(holder.bookmarkView, holder.bookmarksCount, tweet,TweetCommonData.getUserName());
				holder.bookmarkView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View bookmarkView)
					{
						bookmarkView.startAnimation(AnimationUtils.loadAnimation(TweetCo.mContext, R.anim.animation));
						bookmarkView.setSelected(true);
						ViewHolderForBookmarkUpVoteAndHide viewHolderBookMarkUpvoteAndHide = (ViewHolderForBookmarkUpVoteAndHide) bookmarkView.getTag();
						mOnBookmarkClick.onItemClick(viewHolderBookMarkUpvoteAndHide.iterator, true);
					}
				});

				// Finally load the image asynchronously into the ImageView, this also takes care of
				// setting a placeholder image while the background thread runs
				if(tweeter!=null)
				{
					loadProfileImage(tweeter,holder.profilePicImage);
				}
				else
				{
					mImageFetcher.loadImage("A", holder.profilePicImage);
				}

			}
			else
			{
				Log.e(TAG, "TweetUser Not found for tweet with content "+tweet.tweetcontent);
			}
		}
		catch (Exception e) {

		}




		return convertView;
	}
	
	private void setCount(TextView view, String input)
	{
		int count = 0;
		if(!TextUtils.isEmpty(input))
		{
			String[] counts = input.split(";");
			count = counts.length;
		}
		
		view.setText(String.valueOf(count));
		
	}


	private void loadProfileImage(TweetUser tweeter,ImageView imageView)
	{
		Log.d(TAG,"tweeter.profileimageurl="+tweeter.profileimageurl+ "   imageView="+imageView.toString());
		if(TextUtils.isEmpty(tweeter.profileimageurl))
		{
			String initials = Utils.getInitials(tweeter.displayname);
			mImageFetcher.loadImage(initials, imageView);
		}
		else
		{
			mImageFetcher.loadImage(tweeter.profileimageurl, imageView);
		}
	}

	private void loadTweetImage(final Tweet tweet,ImageView imageView)
	{
		if(TextUtils.isEmpty(tweet.imageurl))
		{
			imageView.setVisibility(View.GONE);
		}
		else
		{
			imageView.setVisibility(View.VISIBLE);
			mImageFetcher2.loadImage(tweet.imageurl, imageView);
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TweetCo.mContext, ImageViewActivity.class);
					intent.putExtra(Constants.IMAGE_TO_VIEW, tweet.imageurl);
					intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
					TweetCo.mContext.startActivity(intent);
				}
			});
		}
	}

	private void setUpVoteFlag(ImageView imageView, TextView count, Tweet linkedTweet,String userName)
	{
		if(linkedTweet != null && imageView!=null)
		{
			boolean isCurrentUserUpVoted  = TweetUtils.isStringPresent(linkedTweet.upvoters, userName);
			imageView.setSelected(isCurrentUserUpVoted);
			setCount(count, linkedTweet.upvoters);
		}
	}

	private void setBookMarkFlag(ImageView imageView, TextView count, Tweet linkedTweet,String userName)
	{
		if(linkedTweet != null && imageView!=null)
		{
			boolean didCurrentUserBookmark  = TweetUtils.isStringPresent(linkedTweet.bookmarkers, userName);
			imageView.setSelected(didCurrentUserBookmark);
			setCount(count, linkedTweet.bookmarkers);
		}
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
