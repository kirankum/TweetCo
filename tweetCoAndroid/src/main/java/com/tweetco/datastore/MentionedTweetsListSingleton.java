package com.tweetco.datastore;

import com.tweetco.dao.Tweet;
import com.tweetco.datastore.helper.Helper;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirankumar on 07/07/15.
 */
public enum MentionedTweetsListSingleton {
    INSTANCE;

    private LinkedList<Integer> mentionedTweets = new LinkedList<Integer>();
    private SimpleObservable<List<Integer>> observers = new SimpleObservable<List<Integer>>();

    public void addListener(OnChangeListener<List<Integer>> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<List<Integer>> listener) {
        observers.removeListener(listener);
    }

    public void addMentionedTweetToTop(List<Tweet> list) {
        TweetsListSingleton.INSTANCE.addAll(list);
        mentionedTweets.addAll(0, Helper.getIteratorList(list));
        observers.notifyObservers(mentionedTweets);
    }

    public List<Integer> getMentionedTweets() {
        return mentionedTweets;
    }

    public int getFirstTweetIterator() {
        int iterator = -1;

        if(!mentionedTweets.isEmpty()) {
            iterator = mentionedTweets.getFirst();
        }

        return iterator;
    }

    public void notifyAllObservers() {
        observers.notifyObservers(mentionedTweets);
    }
}
