package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.clients.TweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.IteratorAsKeyReplyTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class TweetRepliesModel {
    private TweetsClient client = new TweetsClient();

    public void refreshTweetsOfTopicFromServer(String sourceTweetIterator) throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kSourceIteratorKey, sourceTweetIterator);
        client.getTweets(ApiInfo.GET_REPLY_TWEETS_FOR_TWEET, obj, tweets, usersList);

        IteratorAsKeyReplyTweetsListSingleton.INSTANCE.updateReplyTweetsListForIteratorFromServer(sourceTweetIterator, tweets);
        UsersListSigleton.INSTANCE.updateUsersListFromServer(usersList);
    }
}
