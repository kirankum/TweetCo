package com.tweetco.clients;

import com.tweetco.TweetCo;
import com.tweetco.twitter.TwitterApp;

/**
 * Created by kirankum on 7/3/2015.
 */
public class TwitterAppClient {

    private static final String TWITTER_CONSUMER_KEY = "JSgcDo14poYM2wvd6ClgYLM1m";
    private static final String TWITTER_SECRET_KEY = "RGYzdbOxkji3kL6YD42HykB1aqO9MBZwqmP0frNTTx1wPaMXZZ";

    public TwitterApp mApp = new TwitterApp(TweetCo.mContext, TWITTER_CONSUMER_KEY, TWITTER_SECRET_KEY);

    public TwitterAppClient(TwitterApp.TwDialogListener listener) {
        mApp.setListener(listener);
    }

    public boolean postTweet(String content) {
        boolean bSuccess = false;

        try {
            mApp.updateStatus(content);
            bSuccess = true;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return bSuccess;
    }
}
