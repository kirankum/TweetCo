package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.clients.TweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.UserAsKeyTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class UserTweetsModel extends TweetsBaseModel {

    public void refreshTweetsFromServer(String username) throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, username);
        obj.addProperty(ApiInfo.kFeedTypeKey, ApiInfo.kUserFeedTypeValue);
        client.getTweets(ApiInfo.GET_TWEETS_FOR_USER, obj, tweets, usersList);

        UserAsKeyTweetsListSingleton.INSTANCE.updateTweetsListForUserFromServer(username, tweets);
        UsersListSigleton.INSTANCE.updateCachedUsersList(usersList);
    }

}
