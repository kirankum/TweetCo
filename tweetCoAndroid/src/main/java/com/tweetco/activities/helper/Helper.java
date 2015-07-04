package com.tweetco.activities.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import com.tweetco.activities.Constants;
import com.tweetco.activities.PostTweetActivity;

import java.io.IOException;

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
        activity.startActivity(intent);
    }

    public static String shortenUrl(String actualUrl) throws IOException {
        String shortUrl = null;
        Urlshortener.Builder builder = new Urlshortener.Builder (AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null);
        Urlshortener urlshortener = builder.build();

        com.google.api.services.urlshortener.model.Url url = new Url();

        url.setLongUrl(actualUrl);

        url = urlshortener.url().insert(url).execute();
        shortUrl = url.getId();

        return shortUrl;
    }
}
