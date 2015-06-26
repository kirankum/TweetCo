package com.tweetco.clients;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class TweetsListClient {

    public void getTweets(String api, JsonObject request, final List<Tweet> tweetList, final List<TweetUser> usersList) throws MalformedURLException {

        MobileServiceClient client = AccountSingleton.INSTANCE.getMobileServiceClient();

        client.invokeApi(api, request, new ApiJsonOperationCallback() {
            @Override
            public void onCompleted(JsonElement jsonObject, Exception exception, ServiceFilterResponse response) {
                if(exception == null)
                {
                    //The received data contains an inner join of tweets and tweet users.
                    //Read them both.
                    Gson gson = new Gson();

                    Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
                    List<Tweet> list = gson.fromJson(jsonObject, collectionType);

                    Type tweetusertype = new TypeToken<List<TweetUser>>(){}.getType();
                    List<TweetUser> tweetUserlist = gson.fromJson(jsonObject, tweetusertype);

                    tweetList.addAll(list);
                    usersList.addAll(tweetUserlist);
                }
                else
                {
                    Log.e("TweetsListClient", "Exception fetching tweets received") ;
                }
            }
        }, true);

    }

}
