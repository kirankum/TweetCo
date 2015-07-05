package com.tweetco.datastore;

import com.tweetco.dao.Tweet;
import com.tweetco.datastore.helper.Helper;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirankumar on 05/07/15.
 */
public enum TodayTweetsListSingleton {
    INSTANCE;

    private LinkedList<Integer> todayTweets = new LinkedList<Integer>();
    private SimpleObservable<List<Integer>> observers = new SimpleObservable<List<Integer>>();

    public void addListener(OnChangeListener<List<Integer>> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<List<Integer>> listener) {
        observers.removeListener(listener);
    }

    public void updateTodayTweetsListFromServer(List<Tweet> list) {
        TweetsListSingleton.INSTANCE.addAll(list);
        todayTweets.clear();
        todayTweets.addAll(Helper.getIteratorList(list));
        observers.notifyObservers(todayTweets);
    }

    public List<Integer> getTodayTweets() {
        return todayTweets;
    }

    public void notifyAllObservers() {
        observers.notifyObservers(todayTweets);
    }
}
