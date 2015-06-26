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

    private HashMap<String, LinkedList<Integer>> iteratorAsKeyReplyTweets = new HashMap<String, LinkedList<Integer>>();
    private HashMap<String, SimpleObservable<LinkedList<Integer>>> observers = new HashMap<String, SimpleObservable<LinkedList<Integer>>>();

    public void addListener(String iterator, OnChangeListener<LinkedList<Integer>> listener) {
        SimpleObservable<LinkedList<Integer>> observer = observers.get(iterator);
        if(observer == null) {
            observer = new SimpleObservable<LinkedList<Integer>>();
        }
        observer.addListener(listener);
    }
    public void removeListener(String iterator, OnChangeListener<LinkedList<Integer>> listener) {
        SimpleObservable<LinkedList<Integer>> observer = observers.get(iterator);
        if(observer == null) {
            observer = new SimpleObservable<LinkedList<Integer>>();
        }
        observer.removeListener(listener);
    }

    public void updateReplyTweetsListForIteratorFromServer(String iterator, List<Tweet> list) {
        LinkedList<Integer> tweetsList = iteratorAsKeyReplyTweets.get(iterator);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
        }

        TweetsListSingleton.INSTANCE.addAll(list);
        tweetsList.clear();
        tweetsList.addAll(Helper.getIteratorList(list));
        SimpleObservable<LinkedList<Integer>> observer = observers.get(iterator);
        if(observer != null) {
            observer.notifyObservers(tweetsList);
        }
    }

    public List<Integer> getRepliesForIterator(String iterator) {
        LinkedList<Integer> tweetsList = iteratorAsKeyReplyTweets.get(iterator);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
        }
        return tweetsList;
    }
}
