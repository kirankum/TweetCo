package com.tweetco.clients;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.tweetco.activities.ApiInfo;
import com.tweetco.dao.Notification;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankumar on 08/07/15.
 */
public class NotificationsClient {

    public List<Notification> refreshNotificationsFromServer() throws MalformedURLException {

        final List<Notification> notifications = new ArrayList<Notification>();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        MobileServiceClient client = AccountSingleton.INSTANCE.getMobileServiceClient();

        client.invokeApi(ApiInfo.GET_NOTIFICATIONS, obj, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
                if(exception == null)
                {
                    //The received data contains an inner join of tweets and tweet users.
                    //Read them both.
                    Gson gson = new Gson();

                    Type collectionType = new TypeToken<List<Notification>>(){}.getType();
                    List<Notification> list = gson.fromJson(jsonObject, collectionType);

                    notifications.addAll(list);
                }
                else
                {
                    Log.e("NotificationsClient", "Exception fetching notifications") ;
                }
            }
        }, true);

        return notifications;
    }

}
