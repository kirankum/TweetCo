package com.tweetco.datastore;

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
public enum IteratorAsKeyReplyTweetsListSingleton {
    INSTANCE;

    private HashMap<String, List<Integer>> iteratorAsKeyReplyTweets = new HashMap<String, List<Integer>>();
    private HashMap<String, SimpleObservable<List<Integer>>> observers = new HashMap<String, SimpleObservable<List<Integer>>>();

    public void addListener(String iterator, OnChangeListener<List<Integer>> listener) {
        SimpleObservable<List<Integer>> observer = observers.get(iterator);
        if(observer == null) {
            observer = new SimpleObservable<List<Integer>>();
            observers.put(iterator, observer);
        }
        observer.addListener(listener);
    }

    public void removeListener(String iterator, OnChangeListener<List<Integer>> listener) {
        SimpleObservable<List<Integer>> observer = observers.get(iterator);
        if(observer != null) {
            observer.removeListener(listener);
        }
    }

    public void updateReplyTweetsListForIteratorFromServer(String iterator, List<Tweet> list) {
        List<Integer> tweetsList = iteratorAsKeyReplyTweets.get(iterator);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
            iteratorAsKeyReplyTweets.put(iterator, tweetsList);
        }

        TweetsListSingleton.INSTANCE.addAll(list);
        tweetsList.clear();
        tweetsList.addAll(Helper.getIteratorList(list));

        SimpleObservable<List<Integer>> observer = observers.get(iterator);
        if(observer != null) {
            observer.notifyObservers(tweetsList);
        }
    }

    public List<Integer> getRepliesForIterator(String iterator) {
        List<Integer> tweetsList = iteratorAsKeyReplyTweets.get(iterator);
        return tweetsList;
    }

    public void notifyAllObservers() {
        for(String iterator : observers.keySet()) {
            SimpleObservable<List<Integer>> observer = observers.get(iterator);
            observer.notifyObservers(iteratorAsKeyReplyTweets.get(iterator));
        }

    }
}
