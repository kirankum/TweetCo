package com.tweetco.datastore;

import com.tweetco.dao.Tweet;
import com.tweetco.datastore.helper.Helper;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public enum BookmarkedTweetsListSingleton {

    INSTANCE;

    private LinkedList<Integer> bookmarkedTweets = new LinkedList<Integer>();
    private SimpleObservable<List<Integer>> observers = new SimpleObservable<List<Integer>>();

    public void addListener(OnChangeListener<List<Integer>> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<List<Integer>> listener) {
        observers.removeListener(listener);
    }

    public void updateBookmarksListFromServer(List<Tweet> list) {
        TweetsListSingleton.INSTANCE.addAll(list);
        bookmarkedTweets.clear();
        bookmarkedTweets.addAll(Helper.getIteratorList(list));
        observers.notifyObservers(bookmarkedTweets);
    }

    public List<Integer> getBookmarkedTweets() {
        return bookmarkedTweets;
    }

    public void notifyAllObservers() {
        observers.notifyObservers(bookmarkedTweets);
    }

}
