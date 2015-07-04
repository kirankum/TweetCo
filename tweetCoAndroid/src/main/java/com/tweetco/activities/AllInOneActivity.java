package com.tweetco.activities;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
import com.tweetco.asynctasks.BitmapWorkerTask;
import com.tweetco.dao.Tweet;
import com.tweetco.database.dao.Account;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.notifications.PushNotificationHandler;
import com.tweetco.roundedimageview.RoundedImageView;
import com.tweetco.tweets.TweetCommonData;
import com.tweetco.utility.UiUtility;


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
	private DrawerLayout mDrawer;
	private NavigationView mNavigationView;
	private ActionBarDrawerToggle mDrawerToggle;
	private Toolbar toolbar;
	private View mProfileBackgroundImage;
	private RoundedImageView mProfilePicImageView;
	private TextView mProfileNameTextView;

	private static final String TAG = "AllInOneActivity";

	private ViewPager mViewPager;
	private static CustomFragmentPagerAdapter mPagerAdapter = null;
	private Account mAccount;
	private ImageFetcher mImageFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.all_in_one_activity_layout);

		NotificationsManager.handleNotifications(this, SENDER_ID, PushNotificationHandler.class);

		mImageFetcher = Utils.getImageFetcher(this, 60, 60);

		// Set a Toolbar to replace the ActionBar.
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Find our drawer view
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = setupDrawerToggle();

		// Tie DrawerLayout events to the ActionBarToggle
		mDrawer.setDrawerListener(mDrawerToggle);

		// Find our drawer view
		mNavigationView = (NavigationView) findViewById(R.id.nvView);
		// Setup drawer view
		setupDrawerContent(mNavigationView);

		customizeActionBar();

	}


	public void customizeActionBar()
	{
		m_actionbar = getSupportActionBar();
		m_actionbar.setDisplayShowHomeEnabled(true);
		m_actionbar.setDisplayShowTitleEnabled(false);
		m_actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
	}

	private ActionBarDrawerToggle setupDrawerToggle() {
		return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
	}

	private void setupDrawerContent(NavigationView navigationView) {

		mProfileBackgroundImage = UiUtility.getView(navigationView, R.id.navProfileBackground);
		mProfilePicImageView = UiUtility.getView(navigationView, R.id.navProfilePic);
		mProfileNameTextView = UiUtility.getView(navigationView, R.id.navProfileName);

		mProfilePicImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AllInOneActivity.this, UserProfileActivity.class);
				intent.putExtra(Constants.USERNAME_STR, TweetCommonData.getUserName());
				AllInOneActivity.this.startActivityForResult(intent, Constants.POSTED_TWEET_REQUEST_CODE);
			}
		});

		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						selectDrawerItem(menuItem);
						return true;
					}
				});
	}

	public void selectDrawerItem(MenuItem menuItem) {
		// Create a new fragment and specify the planet to show based on
		// position
		Fragment fragment = null;

		Class fragmentClass;
		switch(menuItem.getItemId()) {
			case R.id.nav_home:
				//fragmentClass = FirstFragment.class;
				break;

			case R.id.nav_feedback:
				launchPostTweetActivity("#feedback", -1, null);
				break;

			default:
				//fragmentClass = FirstFragment.class;
		}

		/*try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();*/

		// Highlight the selected item, update the title, and close the drawer
		menuItem.setChecked(true);
		setTitle(menuItem.getTitle());
		mDrawer.closeDrawers();
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
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
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

		mProfileNameTextView.setText(mAccount.getUsername());
		mImageFetcher.loadImage(mAccount.profileimageurl, mProfilePicImageView);
		if(!TextUtils.isEmpty(mAccount.profilebgurl)) {
			BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(mAccount.profilebgurl, mProfileBackgroundImage, this);
			bitmapWorkerTask.execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
				mDrawer.openDrawer(GravityCompat.START);
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
