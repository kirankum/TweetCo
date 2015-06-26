package com.tweetco.datastore;

import com.tweetco.dao.Tweet;
import com.tweetco.datastore.helper.Helper;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public enum HomeFeedTweetsListSingleton {
    INSTANCE;

    private LinkedList<Integer> homeFeedTweets = new LinkedList<Integer>();
    private SimpleObservable<LinkedList<Integer>> observers = new SimpleObservable<LinkedList<Integer>>();

    public void addListener(OnChangeListener<LinkedList<Integer>> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<LinkedList<Integer>> listener) {
        observers.removeListener(listener);
    }

    public void addHomeFeedsTweetToTop(List<Tweet> list) {
        TweetsListSingleton.INSTANCE.addAll(list);
        homeFeedTweets.addAll(0, Helper.getIteratorList(list));
        observers.notifyObservers(homeFeedTweets);
    }

    public void addHomeFeedsTweetToBottom(List<Tweet> list) {
        TweetsListSingleton.INSTANCE.addAll(list);
        homeFeedTweets.addAll(homeFeedTweets.isEmpty()? 0: homeFeedTweets.size(), Helper.getIteratorList(list));
        observers.notifyObservers(homeFeedTweets);
    }

    public List<Integer> getHomeFeedTweets() {
        return homeFeedTweets;
    }

    public int getFirstTweetIterator() {
        int iterator = -1;

        if(!homeFeedTweets.isEmpty()) {
            iterator = homeFeedTweets.getFirst();
        }

        return iterator;
    }

    public int getLastTweetIterator() {
        int iterator = -1;

        if(!homeFeedTweets.isEmpty()) {
            iterator = homeFeedTweets.getLast();
        }

        return iterator;
    }
}

