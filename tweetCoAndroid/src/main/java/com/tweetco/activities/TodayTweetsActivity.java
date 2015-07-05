package com.tweetco.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.onefortybytes.R;
import com.tweetco.activities.fragments.TodayTweetsListFragment;
import com.tweetco.activities.fragments.UsersListFragment;
import com.tweetco.utility.UiUtility;

/**
 * Created by kirankumar on 05/07/15.
 */
public class TodayTweetsActivity extends TweetCoBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetstodayactivity);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("Citrix Today");
        }

        if (UiUtility.getView(this, R.id.todayTweetsListFragmentContainer) != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            TodayTweetsListFragment fragment = new TodayTweetsListFragment();
            ft.replace(R.id.todayTweetsListFragmentContainer, fragment);
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