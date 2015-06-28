package com.tweetco.datastore.helper;

import com.tweetco.dao.Tweet;
import com.tweetco.datastore.AccountSingleton;

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

    public static String removeUsername(String usernameList, String username)
    {
        String[] list = usernameList.split(username+";");
        StringBuilder builder = new StringBuilder();
        for(String name: list)
        {
            builder.append(name);
        }

        return builder.toString();
    }

    public static String removeCurrentUsername(String usernameList) {
        return removeUsername(usernameList, AccountSingleton.INSTANCE.getUserName());
    }

    public static String addCurrentUsername(String usernameList) {
        return usernameList + usernameList + ";";
    }

}
