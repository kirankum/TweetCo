package com.tweetco.activities;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.tweetco.activities.fragments.BookmarkedTweetsListFragment;
import com.tweetco.activities.fragments.UserTweetsListFragment;
import com.tweetco.datastore.AccountSingleton;

public class CustomUserProfileFragmentPagerAdapter extends FragmentStatePagerAdapter
{
	public static final int FRAGMENT_COUNT = 2;
	
	 SparseArray<WeakReference<Fragment>> mFragmentsMap = new SparseArray<WeakReference<Fragment>>();

	public CustomUserProfileFragmentPagerAdapter(Context context, FragmentManager fm)
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
			return "Tweets";
		case 1:
			return "Bookmarks";
		}
		return null;
	}

	
	@Override
	public Fragment getItem(int i)
	{
		switch(i)
		{
		case 0:
			UserTweetsListFragment fragment = new UserTweetsListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("username", AccountSingleton.INSTANCE.getUserName());
			fragment.setArguments(bundle);
			return fragment;
		case 1:
			BookmarkedTweetsListFragment fragment1 = new BookmarkedTweetsListFragment();
			return fragment1;
		}
		return null;
	}

	@Override
	public int getCount()
	{
		return FRAGMENT_COUNT;
	}
	
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragmentsMap.put(position, new WeakReference<Fragment>(fragment));
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) 
    {
    	mFragmentsMap.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) 
    {
    	WeakReference<Fragment> weakReference = mFragmentsMap.get(position);
        return weakReference.get();
    }

}
