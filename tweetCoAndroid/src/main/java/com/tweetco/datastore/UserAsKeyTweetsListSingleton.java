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
public enum UserAsKeyTweetsListSingleton {
    INSTANCE;

    private HashMap<String, LinkedList<Integer>> usernameAsKeyTweets = new HashMap<String, LinkedList<Integer>>();
    private HashMap<String, SimpleObservable<LinkedList<Integer>>> observers = new HashMap<String, SimpleObservable<LinkedList<Integer>>>();

    public void addListener(String username, OnChangeListener<LinkedList<Integer>> listener) {
        SimpleObservable<LinkedList<Integer>> observer = observers.get(username);
        if(observer == null) {
            observer = new SimpleObservable<LinkedList<Integer>>();
        }
        observer.addListener(listener);
    }

    public void removeListener(String username, OnChangeListener<LinkedList<Integer>> listener) {
        SimpleObservable<LinkedList<Integer>> observer = observers.get(username);
        if(observer == null) {
            observer = new SimpleObservable<LinkedList<Integer>>();
        }
        observer.removeListener(listener);
    }

    public void updateTweetsListForUserFromServer(String username, List<Tweet> list) {
        LinkedList<Integer> tweetsList = usernameAsKeyTweets.get(username);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
        }

        TweetsListSingleton.INSTANCE.addAll(list);
        tweetsList.clear();
        tweetsList.addAll(Helper.getIteratorList(list));
        SimpleObservable<LinkedList<Integer>> observer = observers.get(username);
        if(observer != null) {
            observer.notifyObservers(tweetsList);
        }
    }

    public List<Integer> getTweetsListForUser(String username) {
        LinkedList<Integer> tweetsList = usernameAsKeyTweets.get(username);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
        }

        return tweetsList;
    }

}
