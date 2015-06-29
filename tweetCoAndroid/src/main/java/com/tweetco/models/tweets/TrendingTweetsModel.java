package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.clients.TweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.TrendingTopicAsKeyTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class TrendingTweetsModel extends TweetsBaseModel {

    public void refreshTweetsOfTopicFromServer(String topic) throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kTrendingTopicKey, topic);
        client.getTweets(ApiInfo.GET_TWEETS_FOR_TREND, obj, tweets, usersList);

        TrendingTopicAsKeyTweetsListSingleton.INSTANCE.updateTweetsListForTrendingTopicFromServer(topic, tweets);
        UsersListSigleton.INSTANCE.updateCachedUsersList(usersList);
    }
}
