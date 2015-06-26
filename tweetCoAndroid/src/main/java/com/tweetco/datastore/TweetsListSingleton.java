package com.tweetco.datastore;

import com.tweetco.dao.Tweet;
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

    public Tweet getTweet(int iterator) throws Exception {
        Tweet tweet = tweetsMap.get(iterator);
        if(tweet == null) {
            //TODO Throw specific exception
            throw new Exception();
        }

        return tweet;
    }
}
