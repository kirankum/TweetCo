package com.tweetco.datastore;

import com.tweetco.dao.Notification;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.interfaces.SimpleObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankumar on 08/07/15.
 */
public enum NotificationsListSingleton {
    INSTANCE;

    public List<Notification> getNotifications() {
        return mNotifications;
    }

    public void updateNotificationsFromServer(List<Notification> notifications) {
        this.mNotifications.clear();
        this.mNotifications.addAll(notifications);
        observers.notifyObservers(mNotifications);
    }

    public void addListener(OnChangeListener<List<Notification>> listener) {
        observers.addListener(listener);
    }
    public void removeListener(OnChangeListener<List<Notification>> listener) {
        observers.removeListener(listener);
    }

    private List<Notification> mNotifications = new ArrayList<Notification>();
    private SimpleObservable<List<Notification>> observers = new SimpleObservable<List<Notification>>();


}
