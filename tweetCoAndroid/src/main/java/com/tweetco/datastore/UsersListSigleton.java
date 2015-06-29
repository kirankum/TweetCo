package com.tweetco.datastore;

import com.tweetco.Exceptions.TweetUserNotFoundException;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.database.dao.Account;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kirankumar on 20/06/15.
 */
public enum UsersListSigleton {
    INSTANCE;

    private Map<String, TweetUser> userListMap = new ConcurrentHashMap<String, TweetUser>();
    private List<TweetUser> usersList = new ArrayList<TweetUser>();

    public List<TweetUser> getUsersList()
    {
        return usersList;
    }

    public TweetUser getUser(String username) throws TweetUserNotFoundException
    {
        TweetUser user = userListMap.get(username);

        if(user == null ) {
            throw new TweetUserNotFoundException();
        }
        return userListMap.get(username);
    }

    public TweetUser getCurrentUser() throws TweetUserNotFoundException
    {
        return getUser(AccountSingleton.INSTANCE.getUserName());
    }

    public void updateUsersListFromServer(List<TweetUser> list)
    {
        usersList.clear();
        userListMap.clear();

        for(TweetUser user : list) {
            if(!usersList.contains(user)) {
                usersList.add(user);
            }
            else {
                int index = usersList.indexOf(user);
                usersList.set(index, user);
            }

            userListMap.put(user.username, user);
        }

    }

    public void updateCachedUsersList(List<TweetUser> list)
    {
        for(TweetUser user : list) {
            if(usersList.contains(user)) {
                int index = usersList.indexOf(user);
                usersList.set(index, user);
                userListMap.put(user.username, user);
            }
        }
    }

    public void followUser(String username) throws TweetUserNotFoundException
    {
        TweetUser user = getUser(username);
        if(user != null)
        {
            user.followers += AccountSingleton.INSTANCE.getUserName()+";";
        }

        TweetUser currentUser = getCurrentUser();
        currentUser.followees += username + ";";
    }

    public void unfollowUser(String username) throws TweetUserNotFoundException
    {
        TweetUser user = getUser(username);
        if(user != null)
        {
            user.followers = removeUsername(user.followers, AccountSingleton.INSTANCE.getUserName());
        }

        TweetUser currentUser = getCurrentUser();
        currentUser.followees = removeUsername(currentUser.followees, username);
    }

    private static String removeUsername(String usernameList, String username) throws TweetUserNotFoundException
    {
        String[] list = usernameList.split(username+";");
        StringBuilder builder = new StringBuilder();
        for(String name: list)
        {
            builder.append(name);
        }

        return builder.toString();
    }
}
