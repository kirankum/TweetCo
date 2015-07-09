package com.tweetco.activities.helper;

import android.content.Intent;
import android.media.Image;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.TweetCo;
import com.tweetco.activities.Constants;
import com.tweetco.activities.ImageViewActivity;
import com.tweetco.activities.Linkify;
import com.tweetco.activities.TweetUtils;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;
import com.tweetco.tweets.TweetCommonData;
import com.tweetco.utility.UiUtility;

/**
 * Created by kirankum on 7/9/2015.
 */
public class TweetUIHolder {

    public static final String TAG = "TweetUIHolder";

    public interface OnTweetItemClick {
        public void onClick(View v, int iterator);
    }

    public ImageView profilePicImage;
    public TextView userhandle;
    public TextView userName;
    public TextView tweetContent;
    public TextView tweetTime;
    public ImageView tweetContentImage;
    public ImageView upvoteView;
    public ImageView bookmarkView;
    public TextView inReplyTo;
    public ImageView replyToTweetButton;
    public TextView upvotesCount;
    public TextView bookmarksCount;
    public int iterator;

    private ImageFetcher mProfileImageFetcher; //Fetches the images

    private ImageFetcher mTweetContentImageFetcher; //Fetches the images

    public TweetUIHolder(View parentView, ImageFetcher profilePicImageFetcher, ImageFetcher tweetContentImageFetcher,
                         final OnTweetItemClick onProfilePicClickCallback,
                         final OnTweetItemClick onTweetClickCallback, final OnTweetItemClick onReplyClickCallback,
                         final OnTweetItemClick onUpvoteClick, final OnTweetItemClick onBookmarkClick, final OnTweetItemClick onHideClick) {

        mProfileImageFetcher = profilePicImageFetcher;
        mTweetContentImageFetcher = tweetContentImageFetcher;

        profilePicImage = (ImageView)parentView.findViewById(R.id.profile_pic);
        profilePicImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        userhandle = (TextView) parentView.findViewById(R.id.handle);
        userName = (TextView) parentView.findViewById(R.id.username);
        tweetContent = (TextView) parentView.findViewById(R.id.tweetcontent);
        tweetTime = (TextView) parentView.findViewById(R.id.time);
        inReplyTo = UiUtility.getView(parentView, R.id.in_reply_to);
        replyToTweetButton = UiUtility.getView(parentView, R.id.replyToTweet);
        upvotesCount = UiUtility.getView(parentView, R.id.tweet_upvoteCount);
        bookmarksCount = UiUtility.getView(parentView, R.id.tweet_bookmarksCount);

        tweetContentImage = (ImageView) parentView.findViewById(R.id.tweet_content_image);
        upvoteView = (ImageView) parentView.findViewById(R.id.upvote);
        bookmarkView = (ImageView) parentView.findViewById(R.id.bookmark);

        profilePicImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onProfilePicClickCallback.onClick(v, iterator);
            }
        });

        tweetContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onTweetClickCallback.onClick(v, iterator);

            }
        });

        replyToTweetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onReplyClickCallback.onClick(v, iterator);
            }
        });

        upvoteView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View upvoteView)
            {
                upvoteView.startAnimation(AnimationUtils.loadAnimation(TweetCo.mContext, R.anim.animation));
                upvoteView.setSelected(true);
                onUpvoteClick.onClick(upvoteView, iterator);
            }

        });

        bookmarkView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View bookmarkView)
            {
                bookmarkView.startAnimation(AnimationUtils.loadAnimation(TweetCo.mContext, R.anim.animation));
                bookmarkView.setSelected(true);
                onBookmarkClick.onClick(bookmarkView, iterator);
            }
        });

    }

    public void loadTweetWithIterator(int iterator) {
        try {
            this.iterator = iterator;

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

                userhandle.setText(username);
                userName.setText(displayName);
                tweetContent.setText(tweet.tweetcontent);
                Linkify.addLinks(tweetContent, Linkify.WEB_URLS | Linkify.HASH_TAGS | Linkify.USER_HANDLE);

                tweetContent.setMovementMethod(new LinkMovementMethod());


                loadTweetImage(tweet.imageurl, tweetContentImage, mTweetContentImageFetcher);

                tweetTime.setText(Utils.getTime(tweet.__createdAt));

                if(!TextUtils.isEmpty(tweet.inreplyto))
                {
                    inReplyTo.setVisibility(View.VISIBLE);
                    inReplyTo.setText("In reply to " + tweet.sourceuser);
                }
                else
                {
                    inReplyTo.setVisibility(View.GONE);
                }

                setUpVoteFlag(upvoteView, upvotesCount, tweet, TweetCommonData.getUserName());

                setBookMarkFlag(bookmarkView, bookmarksCount, tweet, TweetCommonData.getUserName());


                // Finally load the image asynchronously into the ImageView, this also takes care of
                // setting a placeholder image while the background thread runs
                if(tweeter!=null)
                {
                    loadProfileImage(tweeter.profileimageurl, tweeter.displayname, profilePicImage, mProfileImageFetcher);
                }
                else
                {
                    mProfileImageFetcher.loadImage("A", profilePicImage);
                }

            }
            else
            {
                Log.e(TAG, "TweetUser Not found for tweet with content " + tweet.tweetcontent);
            }
        }
        catch (Exception e) {

        }

    }

    private static void loadProfileImage(String profileimageurl, String displayname, ImageView imageView, ImageFetcher imageFetcher)
    {
        if(TextUtils.isEmpty(profileimageurl))
        {
            String initials = Utils.getInitials(displayname);
            imageFetcher.loadImage(initials, imageView);
        }
        else
        {
            imageFetcher.loadImage(profileimageurl, imageView);
        }
    }

    private static void loadTweetImage(final String contentImageurl, ImageView imageView, ImageFetcher imageFetcher)
    {
        if(TextUtils.isEmpty(contentImageurl))
        {
            imageView.setVisibility(View.GONE);
        }
        else
        {
            imageView.setVisibility(View.VISIBLE);
            imageFetcher.loadImage(contentImageurl, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TweetCo.mContext, ImageViewActivity.class);
                    intent.putExtra(Constants.IMAGE_TO_VIEW, contentImageurl);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                    TweetCo.mContext.startActivity(intent);
                }
            });
        }
    }

    private static void setUpVoteFlag(ImageView imageView, TextView count, Tweet linkedTweet,String userName)
    {
        if(linkedTweet != null && imageView!=null)
        {
            boolean isCurrentUserUpVoted  = TweetUtils.isStringPresent(linkedTweet.upvoters, userName);
            imageView.setSelected(isCurrentUserUpVoted);
            setCount(count, linkedTweet.upvoters);
        }
    }

    private static void setBookMarkFlag(ImageView imageView, TextView count, Tweet linkedTweet,String userName)
    {
        if(linkedTweet != null && imageView!=null)
        {
            boolean didCurrentUserBookmark  = TweetUtils.isStringPresent(linkedTweet.bookmarkers, userName);
            imageView.setSelected(didCurrentUserBookmark);
            setCount(count, linkedTweet.bookmarkers);
        }
    }

    private static void setCount(TextView view, String input)
    {
        int count = 0;
        if(!TextUtils.isEmpty(input))
        {
            String[] counts = input.split(";");
            count = counts.length;
        }

        view.setText(String.valueOf(count));

    }
}
