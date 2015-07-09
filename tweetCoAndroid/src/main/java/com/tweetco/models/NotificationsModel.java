package com.tweetco.models;

import com.tweetco.clients.NotificationsClient;
import com.tweetco.dao.Notification;
import com.tweetco.datastore.NotificationsListSingleton;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 08/07/15.
 */
public class NotificationsModel {

    private NotificationsClient client = new NotificationsClient();

    public void refreshNotificationsFromServer() throws MalformedURLException {
        List<Notification> list = client.refreshNotificationsFromServer();
        NotificationsListSingleton.INSTANCE.updateNotificationsFromServer(list);
    }

}
