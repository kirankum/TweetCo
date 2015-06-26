package com.tweetco.datastore.helper;

import com.tweetco.dao.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class Helper {
    public static List<Integer> getIteratorList(List<Tweet> tweetList) {
        List<Integer> list = new ArrayList<Integer>(tweetList.size());

        for(Tweet tweet : tweetList) {
            list.add(tweet.iterator);
        }

        return list;
    }

}
