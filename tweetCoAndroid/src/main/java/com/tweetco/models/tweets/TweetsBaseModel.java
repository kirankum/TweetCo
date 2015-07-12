package com.tweetco.models.tweets;

import com.tweetco.Exceptions.LeaderboardUserNotFoundException;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.clients.TweetsClient;
import com.tweetco.dao.Tweet;
import com.tweetco.datastore.TweetsListSingleton;

import java.net.MalformedURLException;

/**
 * Created by kirankumar on 26/06/15.
 */
public class TweetsBaseModel {

    protected TweetsClient client = new TweetsClient();
    public boolean hasMoreOlderTweets = true;

    public void upvoteTweet(int iterator) throws MalformedURLException, TweetNotFoundException, LeaderboardUserNotFoundException {
        TweetsListSingleton.INSTANCE.upvoteTweet(iterator);
        String tweetOwnerUsername = TweetsListSingleton.INSTANCE.getTweet(iterator).tweetowner;
        client.upvoteTweet(iterator, tweetOwnerUsername, new TweetsClient.IStatusCallback() {
            @Override
            public void success(int iterator) {

            }

            @Override
            public void failure(int iterator) {
                try {
                    TweetsListSingleton.INSTANCE.unUpvoteTweet(iterator);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                } catch (LeaderboardUserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void bookmarkTweet(int iterator) throws MalformedURLException, TweetNotFoundException, LeaderboardUserNotFoundException {
        TweetsListSingleton.INSTANCE.bookmarkTweet(iterator);
        String tweetOwnerUsername = TweetsListSingleton.INSTANCE.getTweet(iterator).tweetowner;
        client.bookmarkTweet(iterator, tweetOwnerUsername, new TweetsClient.IStatusCallback() {
            @Override
            public void success(int iterator) {

            }

            @Override
            public void failure(int iterator) {
                try {
                    TweetsListSingleton.INSTANCE.unBookmarkTweet(iterator);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                } catch (LeaderboardUserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void hideTweet(int iterator) throws MalformedURLException, TweetNotFoundException, LeaderboardUserNotFoundException {
        TweetsListSingleton.INSTANCE.hideTweet(iterator);
        client.hideTweet(iterator, new TweetsClient.IStatusCallback() {
            @Override
            public void success(int iterator) {

            }

            @Override
            public void failure(int iterator) {
                try {
                    TweetsListSingleton.INSTANCE.unHideTweet(iterator);
                } catch (TweetNotFoundException e) {
                    e.printStackTrace();
                } catch (LeaderboardUserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
