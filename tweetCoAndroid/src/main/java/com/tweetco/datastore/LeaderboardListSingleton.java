package com.tweetco.datastore;

import com.tweetco.Exceptions.LeaderboardUserNotFoundException;
import com.tweetco.dao.LeaderboardUser;
import com.tweetco.dao.Tweet;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kirankumar on 20/06/15.
 */
public enum LeaderboardListSingleton {
    INSTANCE;

    private HashMap<String, LeaderboardUser> leaderboardUserHashMap = new HashMap<String, LeaderboardUser>();
    private List<LeaderboardUser> leaderboardUserList = new ArrayList<LeaderboardUser>();
    private SimpleObservable<LeaderboardUser> observers = new SimpleObservable<LeaderboardUser>();

    public void addListener(OnChangeListener<LeaderboardUser> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<LeaderboardUser> listener) {
        observers.removeListener(listener);
    }

    public List<LeaderboardUser> getLeaderboardUserList()
    {
        return leaderboardUserList;
    }

    public void updateLeaderboardUsersListFromServer(List<LeaderboardUser> list)
    {
        leaderboardUserList.removeAll(list);

        leaderboardUserList.addAll(list);

        leaderboardUserHashMap.clear();

        for(LeaderboardUser user : leaderboardUserList) {
            leaderboardUserHashMap.put(user.username, user);
        }

        observers.notifyObservers(null);
    }

    public LeaderboardUser getUser(String username) throws LeaderboardUserNotFoundException {
        LeaderboardUser user = leaderboardUserHashMap.get(username);

        if(user == null) {
            throw new LeaderboardUserNotFoundException();
        }

        return user;
    }

    public void incrementUpvoteCount(String username) throws LeaderboardUserNotFoundException {
        LeaderboardUser user = getUser(username);

        user.upvotes = String.valueOf(Integer.valueOf(user.upvotes) + 1);

        observers.notifyObservers(null);
    }

    public void decrementUpvoteCount(String username) throws LeaderboardUserNotFoundException {
        LeaderboardUser user = getUser(username);

        user.upvotes = String.valueOf(Integer.valueOf(user.upvotes) - 1);

        observers.notifyObservers(null);
    }

    public void incrementBookmarksCount(String username) throws LeaderboardUserNotFoundException {
        LeaderboardUser user = getUser(username);

        user.bookmarks = String.valueOf(Integer.valueOf(user.bookmarks) + 1);

        observers.notifyObservers(null);
    }

    public void decrementBookmarksCount(String username) throws LeaderboardUserNotFoundException {
        LeaderboardUser user = getUser(username);

        user.bookmarks = String.valueOf(Integer.valueOf(user.bookmarks) - 1);

        observers.notifyObservers(null);
    }

}
