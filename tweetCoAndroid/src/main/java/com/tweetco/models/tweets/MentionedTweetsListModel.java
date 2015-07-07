package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.MentionedTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankumar on 07/07/15.
 */
public class MentionedTweetsListModel extends TweetsBaseModel {

    public void refreshLatestMentionedTweetsFromServer() throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        obj.addProperty(ApiInfo.kLastTweetIterator, MentionedTweetsListSingleton.INSTANCE.getFirstTweetIterator());
        obj.addProperty(ApiInfo.kTweetRequestTypeKey, ApiInfo.kNewTweetRequest);
        client.getTweets(ApiInfo.GET_MENTIONED_TWEETS, obj, tweets, usersList);

        MentionedTweetsListSingleton.INSTANCE.addMentionedTweetToTop(tweets);
        UsersListSigleton.INSTANCE.updateCachedUsersList(usersList);
    }
}
