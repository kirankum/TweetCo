package com.tweetco.activities;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.tweetco.activities.fragments.HomeFeedTweetsListFragment;
import com.tweetco.activities.fragments.LeaderboardFragment;
import com.tweetco.activities.fragments.TrendingFragment;
import com.tweetco.activities.fragments.UsersListFragment;

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter
{
	public static final int FRAGMENT_COUNT = 4;
	
	public CustomFragmentPagerAdapter(Context context, FragmentManager fm)
	{
		super(fm);
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		try {
			super.notifyDataSetChanged();
		} catch (Exception e) {
    		e.printStackTrace();
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		
		switch(position)
		{
		case 0:
			return "Home";
		case 1:
			return "LeaderBoard";
		case 2:
			return "Users";
		case 3:
			return "Trending";
		}
		return null;
	}

	
	@Override
	public Fragment getItem(int i)
	{
		switch(i)
		{
		case 0:
			Fragment fragment = new HomeFeedTweetsListFragment();
			return fragment;
		case 1:
			return new LeaderboardFragment();
		case 2:	
			fragment = new UsersListFragment();
			return fragment;
		case 3:
			return new TrendingFragment();
		}
		return null;
	}

	@Override
	public int getCount()
	{
		return FRAGMENT_COUNT;
	}

}
