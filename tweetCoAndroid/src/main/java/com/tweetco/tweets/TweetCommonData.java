package com.tweetco.tweets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.map.LinkedMap;

import android.text.TextUtils;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.activities.TrendingFragment;
import com.tweetco.activities.TweetUtils;
import com.tweetco.dao.Tweet;
import com.tweetco.dao.TweetUser;
import com.tweetco.database.dao.Account;

public class TweetCommonData 
{
	public static Account getAccount()
	{
		return AccountSingleton.INSTANCE.getAccountModel().getAccountCopy();
	}

	public static String getUserName()
	{
		return getAccount().getUsername();
	}

	public static void bookmark(Tweet tweet, String userName)
	{
		if(tweet!=null && !TextUtils.isEmpty(userName))
		{
			if(!TweetUtils.isStringPresent(tweet.bookmarkers, userName))
			{
				tweet.bookmarkers = tweet.bookmarkers +  userName + ";";
			}
		}
	}
	
	public static void like(Tweet tweet, String userName)
	{
		if(tweet!=null && !TextUtils.isEmpty(userName))
		{
			if(!TweetUtils.isStringPresent(tweet.upvoters, userName))
			{
				tweet.upvoters = tweet.upvoters +  userName + ";" ;
			}
		}
	}
}
