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
import com.tweetco.activities.TrendingFragment;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.tweets.TweetCommonData;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankumar on 24/06/15.
 */
public class TrendingListClient {

    public List<TrendingFragment.TrendingTag> getTrendingList() throws MalformedURLException {
        final List<TrendingFragment.TrendingTag> list = new ArrayList<TrendingFragment.TrendingTag>();
        MobileServiceClient mClient = AccountSingleton.INSTANCE.getMobileServiceClient();
        JsonObject obj = new JsonObject();
        mClient.invokeApi(ApiInfo.TRENDING, obj, new ApiJsonOperationCallback() {

            @Override
            public void onCompleted(JsonElement arg0, Exception arg1,
                                    ServiceFilterResponse arg2)
            {

                if(arg1 == null)
                {
                    Gson gson = new Gson();

                    Type collectionType = new TypeToken<List<TrendingFragment.TrendingTag>>(){}.getType();
                    List<TrendingFragment.TrendingTag> trendingTagList = gson.fromJson(arg0, collectionType);

                    if(trendingTagList!=null && !trendingTagList.isEmpty())
                    {
                        list.addAll(trendingTagList);
                    }
                }
                else
                {
                    Log.e("Item clicked", "Exception while loading Trending Tags") ;
                    arg1.printStackTrace();
                }



            }
        },true);

        return list;
    }


}
