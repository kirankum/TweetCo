package com.tweetco.clients;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.tweetco.activities.ApiInfo;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.tweets.TweetCommonData;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankum on 6/26/2015.
 */
public class TweetsClient {

    private final static String TAG = "TweetsClient";

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
                    Log.e("TweetsClient", "Exception fetching tweets received") ;
                }
            }
        }, true);

    }

    public interface IStatusCallback {
        public void success(int iterator);
        public void failure(int iterator);
    }


    public void upvoteTweet(final int iterator, String tweetOwnerUsername, final IStatusCallback callback) throws MalformedURLException {
        MobileServiceClient client = AccountSingleton.INSTANCE.getMobileServiceClient();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        obj.addProperty(ApiInfo.kIteratorKey, iterator);
        obj.addProperty(ApiInfo.kTweetOwner, tweetOwnerUsername);
        client.invokeApi(ApiInfo.UPVOTE, obj, new ApiJsonOperationCallback() {

            @Override
            public void onCompleted(JsonElement arg0, Exception arg1,
                                    ServiceFilterResponse arg2)
            {
                if(arg1 == null)
                {
                    callback.success(iterator);
                }
                else
                {
                    callback.failure(iterator);
                    Log.e(TAG,"Exception upVoting a tweet") ;
                    arg1.printStackTrace();
                }

            }
        },true);
    }

    public void bookmarkTweet(final int iterator, String tweetOwnerUsername, final IStatusCallback callback) throws MalformedURLException {
        MobileServiceClient client = AccountSingleton.INSTANCE.getMobileServiceClient();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        obj.addProperty(ApiInfo.kIteratorKey, iterator);
        obj.addProperty(ApiInfo.kTweetOwner, tweetOwnerUsername);
        client.invokeApi(ApiInfo.BOOKMARK, obj, new ApiJsonOperationCallback() {

            @Override
            public void onCompleted(JsonElement arg0, Exception arg1,
                                    ServiceFilterResponse arg2)
            {
                if(arg1 == null)
                {
                    callback.success(iterator);
                }
                else
                {
                    callback.failure(iterator);
                    Log.e(TAG,"Exception upVoting a tweet") ;
                    arg1.printStackTrace();
                }

            }
        },true);
    }

    public void hideTweet(final int iterator, final IStatusCallback callback) throws MalformedURLException {
        MobileServiceClient client = AccountSingleton.INSTANCE.getMobileServiceClient();

        JsonObject obj = new JsonObject();
        obj.addProperty(ApiInfo.kRequestingUserKey, AccountSingleton.INSTANCE.getUserName());
        obj.addProperty(ApiInfo.kIteratorKey, iterator);
        client.invokeApi(ApiInfo.HIDE_TWEET, obj, new ApiJsonOperationCallback() {

            @Override
            public void onCompleted(JsonElement arg0, Exception arg1,
                                    ServiceFilterResponse arg2)
            {
                if(arg1 == null)
                {
                    callback.success(iterator);
                }
                else
                {
                    callback.failure(iterator);
                    Log.e(TAG,"Exception upVoting a tweet") ;
                    arg1.printStackTrace();
                }

            }
        },true);
    }

    public void postTweet(String content, BitmapDrawable imageContent, int replySourceTweetIterator, String replySourceTweetUsername,
                          boolean bAnonymous, final IStatusCallback callback) throws MalformedURLException {
        MobileServiceClient client = AccountSingleton.INSTANCE.getMobileServiceClient();

        JsonObject element = new JsonObject();
        element.addProperty(ApiInfo.kTweetOwner, AccountSingleton.INSTANCE.getUserName());
        element.addProperty(ApiInfo.kTweetContentKey, content);
        if(!TextUtils.isEmpty(replySourceTweetUsername))
        {
            element.addProperty(ApiInfo.kInReplyToValue, String.valueOf(replySourceTweetIterator));
            element.addProperty(ApiInfo.kSourceUserKey, replySourceTweetUsername);
        }
        if(bAnonymous)
        {
            element.addProperty(ApiInfo.kAnonymous, "TRUE");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if(imageContent != null)
        {
            Bitmap bitmap = imageContent.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG,25,bos);
            byte[] bb = bos.toByteArray();
            String image = Base64.encodeToString(bb, 0);
            element.addProperty("image", image);

        }
        client.invokeApi(ApiInfo.POST_TWEET, element, new ApiJsonOperationCallback() {

            @Override
            public void onCompleted(JsonElement element, Exception exception,
                                    ServiceFilterResponse arg2) {
                if(exception == null)
                {
                    Log.d("postTweet", "TweetPosted");
                    callback.success(-1);
                }
                else
                {
                    Log.e("postTweet", "TweetPost failed");
                    exception.printStackTrace();
                    callback.failure(-1);
                }

            }
        }, true);
    }
}
