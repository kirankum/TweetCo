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
    private HashMap<String, SimpleObservable<List<Integer>>> observers = new HashMap<String, SimpleObservable<List<Integer>>>();

    public void addListener(String username, OnChangeListener<List<Integer>> listener) {
        SimpleObservable<List<Integer>> observer = observers.get(username);
        if(observer == null) {
            observer = new SimpleObservable<List<Integer>>();
            observers.put(username, observer);
        }
        observer.addListener(listener);

    }

    public void removeListener(String username, OnChangeListener<List<Integer>> listener) {
        SimpleObservable<List<Integer>> observer = observers.get(username);
        if(observer != null) {
            observer.removeListener(listener);
        }

    }

    public void updateTweetsListForUserFromServer(String username, List<Tweet> list) {
        LinkedList<Integer> tweetsList = usernameAsKeyTweets.get(username);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
            usernameAsKeyTweets.put(username, tweetsList);
        }

        TweetsListSingleton.INSTANCE.addAll(list);
        tweetsList.clear();
        tweetsList.addAll(Helper.getIteratorList(list));
        SimpleObservable<List<Integer>> observer = observers.get(username);
        if(observer != null) {
            observer.notifyObservers(tweetsList);
        }
    }

    public List<Integer> getTweetsListForUser(String username) {
        return usernameAsKeyTweets.get(username);
    }

}
