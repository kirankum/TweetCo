package com.tweetco.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.onefortybytes.R;
import com.tweetco.activities.fragments.UsersListFragment;
import com.tweetco.utility.UiUtility;

public class UsersListActivity extends TweetCoBaseActivity 
{
	TextView mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userslist);
		
		mTitle = UiUtility.getView(this, R.id.usersListTitle);
		
		String title = getIntent().getStringExtra("title");
		String usersList = getIntent().getStringExtra("usersList");
		
		if(!TextUtils.isEmpty(title))
		{
			mTitle.setText(title);
		}
		
		if(!TextUtils.isEmpty(usersList))
		{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	        UsersListFragment usersListFragment = new UsersListFragment();
	        usersListFragment.addUser(usersList);
	        ft.replace(R.id.usersListFragmentContainer, usersListFragment);
	        ft.commit();
		}
		
		ActionBar actionbar = getSupportActionBar();
		if(actionbar!=null)
		{
			actionbar.setHomeButtonEnabled(true);
			actionbar.setDisplayHomeAsUpEnabled(true);
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
