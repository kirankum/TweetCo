package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.clients.TweetsListClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.IteratorAsKeyReplyTweetsListSingleton;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class TweetModel {
    private TweetsListClient client = new TweetsListClient();

    public void refreshTweetFromServer(String tweetIterator) throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kIteratorKey, tweetIterator);
        client.getTweets(ApiInfo.GET_TWEET_FOR_ITERATOR, obj, tweets, usersList);

        if(!tweets.isEmpty()) {
            TweetsListSingleton.INSTANCE.add(tweets.get(0));
        }
        UsersListSigleton.INSTANCE.updateUsersListFromServer(usersList);
    }
}
