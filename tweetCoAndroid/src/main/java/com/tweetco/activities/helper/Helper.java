package com.tweetco.activities.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tweetco.activities.Constants;
import com.tweetco.activities.PostTweetActivity;

/**
 * Created by kirankumar on 29/06/15.
 */
public class Helper {

    public static void launchPostTweetActivity(Activity activity, String existingString, int replySourceTweetIterator, String replySourceTweetUsername)
    {
        Intent intent = new Intent(activity.getApplicationContext(),PostTweetActivity.class);
        intent.putExtra(Constants.EXISTING_STRING, existingString);
        if(!TextUtils.isEmpty(replySourceTweetUsername))
        {
            intent.putExtra(Constants.INTENT_EXTRA_REPLY_SOURCE_TWEET_USERNAME, replySourceTweetUsername);
            intent.putExtra(Constants.INTENT_EXTRA_REPLY_SOURCE_TWEET_ITERATOR, replySourceTweetIterator);
        }
        activity.startActivityForResult(intent, Constants.POSTED_TWEET_REQUEST_CODE);
    }

}
