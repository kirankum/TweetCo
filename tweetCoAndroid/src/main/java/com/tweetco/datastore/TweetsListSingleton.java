package com.tweetco.datastore;

import com.tweetco.Exceptions.LeaderboardUserNotFoundException;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.dao.Tweet;
import com.tweetco.datastore.helper.Helper;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public enum TweetsListSingleton {
    INSTANCE;

    private HashMap<Integer, Tweet> tweetsMap = new HashMap<Integer, Tweet>();
    private SimpleObservable<Tweet> observers = new SimpleObservable<Tweet>();

    public void addListener(OnChangeListener<Tweet> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<Tweet> listener) {
        observers.removeListener(listener);
    }

    public void notifyAllObservers() {
        observers.notifyObservers(null);

        BookmarkedTweetsListSingleton.INSTANCE.notifyAllObservers();
        HomeFeedTweetsListSingleton.INSTANCE.notifyAllObservers();
        IteratorAsKeyReplyTweetsListSingleton.INSTANCE.notifyAllObservers();
        TrendingTopicAsKeyTweetsListSingleton.INSTANCE.notifyAllObservers();
        UserAsKeyTweetsListSingleton.INSTANCE.notifyAllObservers();
        MentionedTweetsListSingleton.INSTANCE.notifyAllObservers();
        TodayTweetsListSingleton.INSTANCE.notifyAllObservers();
    }

    public void add(Tweet tweet) {
        tweetsMap.put(tweet.iterator, tweet);
        observers.notifyObservers(tweet);
    }

    public void addAll(List<Tweet> tweetList) {
        for(Tweet tweet : tweetList) {
            tweetsMap.put(tweet.iterator, tweet);
        }
        observers.notifyObservers(null);
    }

    public Tweet getTweet(int iterator) throws TweetNotFoundException {
        Tweet tweet = tweetsMap.get(iterator);
        if(tweet == null) {
            throw new TweetNotFoundException();
        }

        return tweet;
    }

    public void upvoteTweet(int iterator) throws TweetNotFoundException, LeaderboardUserNotFoundException {
        Tweet tweet = getTweet(iterator);
        tweet.upvoters = Helper.addCurrentUsername(tweet.upvoters);
        LeaderboardListSingleton.INSTANCE.incrementUpvoteCount(tweet.tweetowner);
        notifyAllObservers();
    }

    public void unUpvoteTweet(int iterator) throws TweetNotFoundException, LeaderboardUserNotFoundException {
        Tweet tweet = getTweet(iterator);
        tweet.upvoters = Helper.removeCurrentUsername(tweet.upvoters);
        LeaderboardListSingleton.INSTANCE.decrementUpvoteCount(tweet.tweetowner);
        notifyAllObservers();
    }

    public void bookmarkTweet(int iterator) throws TweetNotFoundException, LeaderboardUserNotFoundException {
        Tweet tweet = getTweet(iterator);
        tweet.bookmarkers = Helper.addCurrentUsername(tweet.bookmarkers);
        LeaderboardListSingleton.INSTANCE.incrementBookmarksCount(tweet.tweetowner);
        notifyAllObservers();
    }

    public void unBookmarkTweet(int iterator) throws TweetNotFoundException, LeaderboardUserNotFoundException {
        Tweet tweet = getTweet(iterator);
        tweet.bookmarkers = Helper.removeCurrentUsername(tweet.bookmarkers);
        LeaderboardListSingleton.INSTANCE.decrementBookmarksCount(tweet.tweetowner);
        notifyAllObservers();
    }

    public void hideTweet(int iterator ) throws TweetNotFoundException, LeaderboardUserNotFoundException {
        Tweet tweet = getTweet(iterator);
        tweet.hiders = Helper.addCurrentUsername(tweet.hiders);
        notifyAllObservers();
    }

    public void unHideTweet(int iterator ) throws TweetNotFoundException, LeaderboardUserNotFoundException {
        Tweet tweet = getTweet(iterator);
        tweet.hiders = Helper.removeCurrentUsername(tweet.hiders);
        notifyAllObservers();
    }

}
