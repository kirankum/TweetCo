package com.tweetco;

import android.app.Application;
import android.content.Context;

public class TweetCo extends android.support.multidex.MultiDexApplication
{
	public static String APP_KEY = "PImqNtOVaoZFzGrQDAcrXwQnpLuZCf69";
	public static String APP_URL = "https://tweetcotest.azure-mobile.net/";
	
	public static Context mContext;
	
	private static void init(Context context)
	{
		mContext = context;
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		android.support.multidex.MultiDex.install(this);
	}
	
	@Override
    public void onCreate() 
	{
        super.onCreate();
        init(this.getApplicationContext());
	}
}
