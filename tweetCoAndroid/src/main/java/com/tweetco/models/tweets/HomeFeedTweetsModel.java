package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.clients.TweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.HomeFeedTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class HomeFeedTweetsModel {

    private TweetsClient client = new TweetsClient();

    public void refreshLatestTweetsFromServer() throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        obj.addProperty(ApiInfo.kFeedTypeKey, ApiInfo.kHomeFeedTypeValue);
        obj.addProperty(ApiInfo.kLastTweetIterator, HomeFeedTweetsListSingleton.INSTANCE.getFirstTweetIterator());
        obj.addProperty(ApiInfo.kTweetRequestTypeKey, ApiInfo.kNewTweetRequest);
        client.getTweets(ApiInfo.GET_TWEETS_FOR_USER, obj, tweets, usersList);

        HomeFeedTweetsListSingleton.INSTANCE.addHomeFeedsTweetToTop(tweets);
        UsersListSigleton.INSTANCE.updateUsersListFromServer(usersList);
    }

    public void refreshOlderTweetsFromServer() throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        obj.addProperty(ApiInfo.kFeedTypeKey, ApiInfo.kHomeFeedTypeValue);
        obj.addProperty(ApiInfo.kLastTweetIterator, HomeFeedTweetsListSingleton.INSTANCE.getLastTweetIterator());
        obj.addProperty(ApiInfo.kTweetRequestTypeKey, ApiInfo.kOldTweetRequest);
        client.getTweets(ApiInfo.GET_TWEETS_FOR_USER, obj, tweets, usersList);

        HomeFeedTweetsListSingleton.INSTANCE.addHomeFeedsTweetToBottom(tweets);
        UsersListSigleton.INSTANCE.updateUsersListFromServer(usersList);
    }
}
