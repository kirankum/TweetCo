package com.tweetco.activities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagedisplay.util.AsyncTask;
import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.onefortybytes.R;
import com.tweetco.dao.Tweet;
import com.tweetco.database.dao.Account;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.notifications.PushNotificationHandler;
import com.tweetco.tweets.TweetCommonData;



public class AllInOneActivity extends TweetCoBaseActivity 
{

	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		this.setIntent(intent);
	}


	public static final String SENDER_ID = "721884328218";

	private ActionBar m_actionbar;

	private static final String TAG = "AllInOneActivity";

	private ViewPager mViewPager;
	private static CustomFragmentPagerAdapter mPagerAdapter = null;
	private ImageView mProfileImageView;
	private TextView mProfileUsernameTextView;
	private Account mAccount;
	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.all_in_one_activity_layout);

		NotificationsManager.handleNotifications(this, SENDER_ID, PushNotificationHandler.class);

		mImageFetcher = Utils.getImageFetcher(this, 50, 50);

		customizeActionBar();

		mProfileImageView = (ImageView)m_actionbar.getCustomView().findViewById(R.id.imageView1);
		mProfileImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AllInOneActivity.this, UserProfileActivity.class);
				intent.putExtra(Constants.USERNAME_STR, TweetCommonData.getUserName());
				AllInOneActivity.this.startActivityForResult(intent, Constants.POSTED_TWEET_REQUEST_CODE);
			}
		});

		mProfileUsernameTextView = (TextView)m_actionbar.getCustomView().findViewById(R.id.title);
	}


	public void customizeActionBar()
	{
		m_actionbar = getSupportActionBar();
		m_actionbar.setDisplayShowHomeEnabled(false);
		m_actionbar.setDisplayShowTitleEnabled(false);
		View customView =  LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		m_actionbar.setCustomView(customView, params);
		m_actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ~ActionBar.DISPLAY_SHOW_HOME);
		m_actionbar.setDisplayHomeAsUpEnabled(false);	
	}

	private void hideKeyboard() 
	{   
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void initializePager()
	{	
		// init pager
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				hideKeyboard();
			}
		});

		mPagerAdapter = new CustomFragmentPagerAdapter(this.getApplicationContext(), getSupportFragmentManager());
		
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount() - 1);
		mViewPager.setCurrentItem(0);
	}

	public FragmentStatePagerAdapter getPagerAdapter()
	{
		return mPagerAdapter;
	}

	@Override
	protected void onResume() 
	{
		super.onResume();

		if(mAccount == null) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					mAccount = AccountSingleton.INSTANCE.getAccountModel().getAccountCopy();
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					initActivity();
				}
			}.execute();
		}
		else {
			initActivity();
		}
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == Constants.POSTED_TWEET_REQUEST_CODE)
		{
			if(resultCode == RESULT_OK)
			{

			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void initActivity()
	{
		initializePager();

		mProfileUsernameTextView.setText(mAccount.getUsername());
		mImageFetcher.loadImage(mAccount.profileimageurl, mProfileImageView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.launcher, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_about:
	        	Intent intent = new Intent(this.getApplicationContext(),AboutActivity.class);
	    		this.startActivity(intent);
	            return true;
	        case R.id.action_feedback:
	        	launchPostTweetActivity("#feedback", -1, null);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void launchPostTweetActivity(String existingString, int replySourceTweetIterator, String replySourceTweetUsername)
	{
		Intent intent = new Intent(this.getApplicationContext(),PostTweetActivity.class);
		intent.putExtra(Constants.EXISTING_STRING, existingString);
		if(!TextUtils.isEmpty(replySourceTweetUsername))
		{
			intent.putExtra(Constants.INTENT_EXTRA_REPLY_SOURCE_TWEET_USERNAME, replySourceTweetUsername);
			intent.putExtra(Constants.INTENT_EXTRA_REPLY_SOURCE_TWEET_ITERATOR, replySourceTweetIterator);
		}
		this.startActivityForResult(intent, Constants.POSTED_TWEET_REQUEST_CODE);
	}

}
