package com.tweetco.models.tweets;

import com.google.gson.JsonObject;
import com.tweetco.activities.ApiInfo;
import com.tweetco.clients.TweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.BookmarkedTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class BookmarkedTweetsModel {

    private TweetsClient client = new TweetsClient();

    public void refreshBookmarkedTweetsListFromServer() throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        client.getTweets(ApiInfo.GET_BOOKMARKED_TWEETS, obj, tweets, usersList);

        BookmarkedTweetsListSingleton.INSTANCE.updateBookmarksListFromServer(tweets);
        UsersListSigleton.INSTANCE.updateCachedUsersList(usersList);
    }

}
