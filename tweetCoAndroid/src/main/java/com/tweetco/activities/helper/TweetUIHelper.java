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
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;
import com.tweetco.tweets.TweetCommonData;
import com.tweetco.utility.UiUtility;

/**
 * Created by kirankum on 7/9/2015.
 */
public class TweetUIHelper {

    public static class ViewHolder
    {
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
    }

    private static class ViewHolderForBookmarkUpVoteAndHide
    {
        int iterator;
    }

    public static ViewHolder initTweetUI(View parentView, int iterator, ImageFetcher profilePicImageFetcher, ImageFetcher tweetContentImageFetcher,
                                         final View.OnClickListener onProfilePicClickCallback,
                                         final View.OnClickListener onTweetClickCallback, final View.OnClickListener onReplyClickCallback,
                                         final View.OnClickListener onUpvoteClick, final View.OnClickListener onBookmarkClick, final View.OnClickListener onHideClick) {
        ViewHolder viewholder = new ViewHolder();

        viewholder.profilePicImage = (ImageView)parentView.findViewById(R.id.profile_pic);
        viewholder.profilePicImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewholder.userhandle = (TextView) parentView.findViewById(R.id.handle);
        viewholder.userName = (TextView) parentView.findViewById(R.id.username);
        viewholder.tweetContent = (TextView) parentView.findViewById(R.id.tweetcontent);
        viewholder.tweetTime = (TextView) parentView.findViewById(R.id.time);
        viewholder.inReplyTo = UiUtility.getView(parentView, R.id.in_reply_to);
        viewholder.replyToTweetButton = UiUtility.getView(parentView, R.id.replyToTweet);
        viewholder.upvotesCount = UiUtility.getView(parentView, R.id.tweet_upvoteCount);
        viewholder.bookmarksCount = UiUtility.getView(parentView, R.id.tweet_bookmarksCount);

        viewholder.tweetContentImage = (ImageView) parentView.findViewById(R.id.tweet_content_image);
        viewholder.upvoteView = (ImageView) parentView.findViewById(R.id.upvote);
        viewholder.bookmarkView = (ImageView) parentView.findViewById(R.id.bookmark);

        viewholder.profilePicImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onProfilePicClickCallback.onClick(v);
            }
        });

        viewholder.tweetContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onTweetClickCallback.onClick(v);

            }
        });

        viewholder.replyToTweetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onReplyClickCallback.onClick(v);
            }
        });

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

                viewholder.userhandle.setText(username);
                viewholder.userName.setText(displayName);
                viewholder.tweetContent.setText(tweet.tweetcontent);
                Linkify.addLinks(viewholder.tweetContent, Linkify.WEB_URLS | Linkify.HASH_TAGS | Linkify.USER_HANDLE);

                viewholder.tweetContent.setMovementMethod(new LinkMovementMethod());


                loadTweetImage(tweet.imageurl, viewholder.tweetContentImage, tweetContentImageFetcher);

                viewholder.tweetTime.setText(Utils.getTime(tweet.__createdAt));

                ViewHolderForBookmarkUpVoteAndHide viewHolderBookMarkUpvoteAndHide = new ViewHolderForBookmarkUpVoteAndHide();
                viewHolderBookMarkUpvoteAndHide.iterator = tweet.iterator;

                if(!TextUtils.isEmpty(tweet.inreplyto))
                {
                    viewholder.inReplyTo.setVisibility(View.VISIBLE);
                    viewholder.inReplyTo.setText("In reply to " + tweet.sourceuser);
                }
                else
                {
                    viewholder.inReplyTo.setVisibility(View.GONE);
                }

                //UpVote ImageView

                viewholder.upvoteView.setTag(viewHolderBookMarkUpvoteAndHide);
                setUpVoteFlag(viewholder.upvoteView, viewholder.upvotesCount, tweet, TweetCommonData.getUserName());
                viewholder.upvoteView.setOnClickListener(new View.OnClickListener()
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


                viewholder.bookmarkView.setTag(viewHolderBookMarkUpvoteAndHide);
                setBookMarkFlag(viewholder.bookmarkView, viewholder.bookmarksCount, tweet,TweetCommonData.getUserName());
                viewholder.bookmarkView.setOnClickListener(new View.OnClickListener()
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
                    loadProfileImage(tweeter.profileimageurl, tweeter.displayname, viewholder.profilePicImage, profilePicImageFetcher);
                }
                else
                {
                    profilePicImageFetcher.loadImage("A", viewholder.profilePicImage);
                }

            }
            else
            {
                Log.e(TAG, "TweetUser Not found for tweet with content " + tweet.tweetcontent);
            }
        }
        catch (Exception e) {

        }

        return viewholder;
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
}
