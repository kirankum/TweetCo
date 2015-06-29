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
public enum TrendingTopicAsKeyTweetsListSingleton {
    INSTANCE;

    private HashMap<String, LinkedList<Integer>> trendingTopicAsKeyTweets = new HashMap<String, LinkedList<Integer>>();
    private HashMap<String, SimpleObservable<List<Integer>>> observers = new HashMap<String, SimpleObservable<List<Integer>>>();

    public void addListener(String topic, OnChangeListener<List<Integer>> listener) {
        SimpleObservable<List<Integer>> observer = observers.get(topic);
        if(observer == null) {
            observer = new SimpleObservable<List<Integer>>();
            observers.put(topic, observer);
        }
        observer.addListener(listener);
    }

    public void removeListener(String iterator, OnChangeListener<List<Integer>> listener) {
        SimpleObservable<List<Integer>> observer = observers.get(iterator);
        if(observer != null) {
            observer.removeListener(listener);
        }
    }

    public void updateTweetsListForTrendingTopicFromServer(String topic, List<Tweet> list) {
        LinkedList<Integer> tweetsList = trendingTopicAsKeyTweets.get(topic);
        if(tweetsList == null) {
            tweetsList = new LinkedList<Integer>();
            trendingTopicAsKeyTweets.put(topic, tweetsList);
        }

        TweetsListSingleton.INSTANCE.addAll(list);
        tweetsList.clear();
        tweetsList.addAll(Helper.getIteratorList(list));
        SimpleObservable<List<Integer>> observer = observers.get(topic);
        if(observer != null) {
            observer.notifyObservers(tweetsList);
        }
    }

    public List<Integer> getTweetsListForTopic(String topic) {
        return trendingTopicAsKeyTweets.get(topic);
    }
}
