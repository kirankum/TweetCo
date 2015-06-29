package com.tweetco.activities;

import java.net.MalformedURLException;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.imagedisplay.util.AsyncTask;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.tweetco.TweetCo;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.database.dao.Account;
import com.tweetco.tweets.TweetCommonData;

public abstract class TweetCoBaseActivity extends ActionBarActivity 
{
	private static final String TAG = "TweetCoBaseActivity";
	public static boolean isAppInForeground = false;
	public static Activity topActivity = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		topActivity =  this;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		isAppInForeground = true;
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		isAppInForeground = false;
	}
}
