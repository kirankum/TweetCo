package com.tweetco.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.onefortybytes.R;
import com.tweetco.activities.fragments.TrendingTopicTweetsListFragment;
import com.tweetco.utility.UiUtility;

public class TrendingFragmentActivity extends TweetCoBaseActivity 
{
	private String mTag = null;

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public TrendingFragmentActivity() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.trendingfragmentactivity);

		mTag = getIntent().getExtras().getString(Constants.TREND_TAG_STR);

		ActionBar actionbar = getSupportActionBar();
		if(actionbar!=null)
		{
			actionbar.setHomeButtonEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setTitle(mTag);
		}


		if(UiUtility.getView(this, R.id.trendingTweetsListFragmentContainer) != null)
		{
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Fragment fragment = new TrendingTopicTweetsListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("topic", mTag);
			fragment.setArguments(bundle);
			ft.replace(R.id.trendingTweetsListFragmentContainer, fragment);
			ft.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}




}
