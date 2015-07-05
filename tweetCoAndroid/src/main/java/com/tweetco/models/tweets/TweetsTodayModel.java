package com.tweetco.models.tweets;

import com.tweetco.clients.TodayTweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.TodayTweetsListSingleton;
import com.tweetco.datastore.UsersListSigleton;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankumar on 05/07/15.
 */
public class TweetsTodayModel {

    private TodayTweetsClient client = new TodayTweetsClient();

    public void refreshTodayTweetsListFromServer() throws MalformedURLException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TweetUser> usersList = new ArrayList<TweetUser>();

        client.refreshTodayTweetsListFromServer(tweets, usersList);

        TodayTweetsListSingleton.INSTANCE.updateTodayTweetsListFromServer(tweets);
        UsersListSigleton.INSTANCE.updateCachedUsersList(usersList);
    }
}
